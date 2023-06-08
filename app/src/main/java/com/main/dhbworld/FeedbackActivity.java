package com.main.dhbworld;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.Feedback.FeedbackAdapter;
import com.main.dhbworld.Feedback.NewFeedbackActivity;
import com.main.dhbworld.Feedback.data.FeedbackIssue;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Thread.ThreadWithUiUpdate;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private RecyclerView recyclerView;
    private TextView textViewNothingHere;
    private FloatingActionButton addFeedbackButton;

    private final ArrayList<FeedbackIssue> feedbackArrayList = new ArrayList<>();

    private boolean issueListingInProgress = true;

    private String repo;
    private String token;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.github) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/inFumumVerti/DHBWorld/issues"));
                FeedbackActivity.this.startActivity(browserIntent);
            }
            return true;
        });

        setupViews();
        setupClickListener();

        requestUserAndToken(new UserAndTokenListener() {
            @Override
            public void onData(String userId, String token) {
                FeedbackActivity.this.token = token;
                FeedbackActivity.this.userId = userId;
                issueListingInProgress = false;
                listIssues();
                addFeedbackButton.setEnabled(true);
            }

            @Override
            public void onError() {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.something_went_wrong), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickListener() {
        addFeedbackButton.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, NewFeedbackActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("repo", repo);
            startActivity(intent);
        });
    }

    private void setupViews() {
        progressIndicator = findViewById(R.id.feedback_activity_progressindicator);
        addFeedbackButton = findViewById(R.id.add_feedback);
        recyclerView = findViewById(R.id.feedback_recyclerview);
        textViewNothingHere = findViewById(R.id.feedback_nothing_here);
        addFeedbackButton.setEnabled(false);
    }

    private void requestUserAndToken(UserAndTokenListener userAndTokenListener) {
        FirebaseFirestore firestore= FirebaseFirestore.getInstance();
        DocumentReference github = firestore.collection("General").document("GithubToken");
        Utilities utilities = new Utilities(this);

        utilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                utilities.saveFCMToken();
                github.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc= task.getResult();
                        token = doc.getString("token");
                        repo = doc.getString("repo");
                        userAndTokenListener.onData(user.getUid(), token);
                    } else {
                        userAndTokenListener.onError();
                    }
                });
            }

            @Override
            public void onSignInError() {
                userAndTokenListener.onError();
            }
        });
        utilities.signIn();
    }

    private void listIssues() {
        if (issueListingInProgress) {
            return;
        }
        issueListingInProgress = true;
        new ThreadWithUiUpdate(() -> {
            try {
                populateArrayList();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.something_went_wrong), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }).afterOnUiThread(this, () -> {
            setupRecyclerView();
            progressIndicator.setVisibility(View.GONE);
            issueListingInProgress = false;
            if (feedbackArrayList.size() == 0) {
                textViewNothingHere.setVisibility(View.VISIBLE);
            } else {
                textViewNothingHere.setVisibility(View.GONE);
            }
        }).start();
    }

    private void setupRecyclerView() {
        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(feedbackArrayList, token, repo, FeedbackActivity.this);
        recyclerView.setAdapter(feedbackAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(FeedbackActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(FeedbackActivity.this, DividerItemDecoration.VERTICAL));
    }

    private void populateArrayList() throws IOException {
        feedbackArrayList.clear();

        List<GHIssue> issues = getGhIssues();

        for (GHIssue issue : issues) {
            String title = issue.getTitle();
            if (title.endsWith("#" + userId)) {
                feedbackArrayList.add(new FeedbackIssue(issue));
            }
        }
    }

    private List<GHIssue> getGhIssues() throws IOException {
        GitHub gitHub = new GitHubBuilder().withOAuthToken(token).build();
        GHRepository repository = gitHub.getRepository(repo);

        return repository.getIssues(GHIssueState.OPEN);
    }

    @Override
    protected void onResume() {
        progressIndicator.setVisibility(View.VISIBLE);
        listIssues();
        super.onResume();
    }

    interface UserAndTokenListener {
        void onData(String userId, String token);
        void onError();
    }
}