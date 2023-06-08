package com.main.dhbworld.Feedback;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.R;
import com.main.dhbworld.Thread.ThreadWithUiUpdate;
import com.main.dhbworld.Utilities.ProgressDialog;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewFeedbackActivity extends AppCompatActivity {

    private AutoCompleteTextView feedbackType;
    private TextInputEditText feedbackName;
    private TextInputEditText feedbackTitle;
    private TextInputEditText feedbackDescription;
    private MaterialButton sendButton;

    private String repo;
    private String token;
    private String userId;

    private final Map<String, String> typeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feedback);

        setupViews();
        setupMap();
        setupClickListener();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        token = bundle.getString("token");
        repo = bundle.getString("repo");

        ProgressDialog progressDialog = new ProgressDialog(this)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .show();

        getUserId(new UserIdListener() {
            @Override
            public void onUserIdReceived(String userId) {
                NewFeedbackActivity.this.userId = userId;
                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.something_went_wrong), BaseTransientBottomBar.LENGTH_LONG).show();
                sendButton.setEnabled(false);
            }
        });

    }

    private void setupMap() {
        String[] typesTranslated = getResources().getStringArray(R.array.feedback_types);
        typeMap.put(typesTranslated[0], "bug");
        typeMap.put(typesTranslated[1], "enhancement");
        typeMap.put(typesTranslated[2], "feedback");
        typeMap.put(typesTranslated[3], "help wanted");
    }

    private void setupClickListener() {
        sendButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(NewFeedbackActivity.this)
                .setTitle(R.string.important)
                .setMessage(R.string.feedback_policy)
                .setPositiveButton(R.string.send, (dialog, which) -> initAndCreateIssue())
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show());
    }

    private void initAndCreateIssue() {
        String[] inputData = getInputData();

        if (inputData == null) {
            return;
        }

        String type = inputData[0];
        String name = inputData[1];
        String title = inputData[2];
        String description = inputData[3];

        if (type.isEmpty() || name.isEmpty() || title.isEmpty() || description.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.please_fill_in_everything), BaseTransientBottomBar.LENGTH_LONG).show();
            return;
        }

        String phoneData = getPhoneData();
        String body = description + "\n(" + name + ")" + phoneData + "\n\nType: " + typeMap.get(type);

        ProgressDialog progressDialog = new ProgressDialog(NewFeedbackActivity.this)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .show();

        new ThreadWithUiUpdate(() -> {
            try {
                createIssue(title, body);
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.something_went_wrong), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }).afterOnUiThread(NewFeedbackActivity.this, () -> {
            progressDialog.dismiss();
            finish();
        }).start();
    }

    private String[] getInputData() {
        String[] inputData = new String[4];
        Editable typeEditable = feedbackType.getText();
        Editable nameEditable = feedbackName.getText();
        Editable titleEditable = feedbackTitle.getText();
        Editable descriptionEditable = feedbackDescription.getText();

        if (typeEditable == null || nameEditable == null || titleEditable == null || descriptionEditable == null) {
            return null;
        }

        inputData[0] = typeEditable.toString();
        inputData[1] = nameEditable.toString();
        inputData[2] = titleEditable.toString();
        inputData[3] = descriptionEditable.toString();

        return inputData;
    }

    private void createIssue(String title, String body) throws IOException {
        GHRepository repository = initializeRepo();
        repository.createIssue(title + " #" + userId)
                .body(body)
                .create();
    }

    private GHRepository initializeRepo() throws IOException {
        GitHub gitHub = new GitHubBuilder().withOAuthToken(token).build();
        return gitHub.getRepository(repo);
    }

    private void getUserId(UserIdListener userIdListener) {
        Utilities utilities = new Utilities(getApplicationContext());
        utilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                userIdListener.onUserIdReceived(user.getUid());
            }

            @Override
            public void onSignInError() {
                userIdListener.onError();
            }
        });
        utilities.signIn();
    }

    private String getPhoneData() {
        return "\n\nAndroid Version: " +
                Build.VERSION.RELEASE +
                "\nManufacturer: " +
                Build.MANUFACTURER +
                "\nBrand: " +
                Build.BRAND +
                "\nModel: " +
                Build.MODEL +
                "\nDevice: " +
                Build.DEVICE;
    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        feedbackType = findViewById(R.id.feedbackType);
        String[] types = getResources().getStringArray(R.array.feedback_types);
        feedbackType.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_list_item, types));

        feedbackName = findViewById(R.id.feedbackName);
        feedbackTitle = findViewById(R.id.feedbackTitle);
        feedbackDescription = findViewById(R.id.feedbackDescription);

        sendButton = findViewById(R.id.sendFeedbackButton);
    }

    private interface UserIdListener {
        void onUserIdReceived(String userId);
        void onError();
    }
}
