package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextInputLayout feedbackNameLayout = findViewById(R.id.feedbackNameLayout);
        TextInputLayout feedbackEmailLayout = findViewById(R.id.feedbackEmailLayout);
        TextInputLayout feedbackTitleLayout = findViewById(R.id.feedbackTitleLayout);
        TextInputLayout feedbackDescriptionLayout = findViewById(R.id.feedbackDescriptionLayout);

        TextInputEditText feedbackName = findViewById(R.id.feedbackName);
        TextInputEditText feedbackEmail = findViewById(R.id.feedbackEmail);
        TextInputEditText feedbackTitle = findViewById(R.id.feedbackTitle);
        TextInputEditText feedbackDescription = findViewById(R.id.feedbackDescription);

        MaterialButton sendButton = findViewById(R.id.sendFeedbackButton);
        MaterialButton openGitHubButton = findViewById(R.id.openGithubButton);

        removeErrorOnType(feedbackName, feedbackNameLayout);
        removeErrorOnType(feedbackEmail, feedbackEmailLayout);
        removeErrorOnType(feedbackTitle, feedbackTitleLayout);
        removeErrorOnType(feedbackDescription, feedbackDescriptionLayout);

        sendButton.setOnClickListener(view -> {
            String name = feedbackName.getText().toString();
            String email = feedbackEmail.getText().toString();
            String title = feedbackTitle.getText().toString();
            String description = feedbackDescription.getText().toString();

            if (name.isEmpty()) {
                feedbackNameLayout.setError("Darf nicht leer sein");
            }
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //TODO stimmt noch nicht
                feedbackEmailLayout.setError("Ungültige E-Mail Adresse");
            }
            if (email.isEmpty()) {
                feedbackEmailLayout.setError("Darf nicht leer sein");
            }
            if (title.isEmpty()) {
                feedbackTitleLayout.setError("Darf nicht leer sein");
            }

            //TODO: INFOS SENDEN

            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("https://dhbworld.blitzdose.de/create-issue.php");
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setRequestProperty( "Content-type", "application/x-www-form-urlencoded");

                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                        String[] data = new String[4];
                        data[0] = "name=" + name;
                        data[1] = "email=" + email;
                        data[2] = "title=" + title;
                        data[3] = "body=" + description;

                        writer.write(String.join("&", data));
                        writer.flush();
                        writer.close();

                        int status = urlConnection.getResponseCode();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status == 200) {
                                    Toast.makeText(FeedbackActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(FeedbackActivity.this, status, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        });

        openGitHubButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/inFumumVerti/DHBWorld/issues"));
            try {
                FeedbackActivity.this.startActivity(browserIntent);
            } catch (Exception ignored) { }
        });
    }

    private void removeErrorOnType(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}