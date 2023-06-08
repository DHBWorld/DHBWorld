package com.main.dhbworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Feedback.FeedbackAdapter;
import com.main.dhbworld.Feedback.FeedbackIssue;
import com.main.dhbworld.Feedback.NewFeedbackActivity;
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
    private FloatingActionButton addFeedbackButton;
    private RecyclerView recyclerView;

    private ArrayList<FeedbackIssue> feedbackArrayList = new ArrayList<>();

    private boolean issueListingInProgress = false;

    private String token = "";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressIndicator = findViewById(R.id.feedback_activity_progressindicator);
        addFeedbackButton = findViewById(R.id.add_feedback);
        recyclerView = findViewById(R.id.feedback_recyclerview);
        addFeedbackButton.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, NewFeedbackActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        Utilities utilities = new Utilities(this);
        utilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                utilities.saveFCMToken();
                userId = user.getUid();
                listIssues();
            }

            @Override
            public void onSignInError() {

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
                feedbackArrayList.clear();

                List<GHIssue> issues = getGhIssues();

                for (GHIssue issue : issues) {
                    String title = issue.getTitle();
                    if (title.endsWith("#" + userId)) {
                        feedbackArrayList.add(new FeedbackIssue(issue));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).afterOnUiThread(this, () -> {
            FeedbackAdapter feedbackAdapter = new FeedbackAdapter(feedbackArrayList, token, FeedbackActivity.this);
            recyclerView.setAdapter(feedbackAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(FeedbackActivity.this, LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new DividerItemDecoration(FeedbackActivity.this, DividerItemDecoration.VERTICAL));
            progressIndicator.setVisibility(View.GONE);
            issueListingInProgress = false;
        }).start();
    }

    private List<GHIssue> getGhIssues() throws IOException {
        GitHub gitHub = new GitHubBuilder().withOAuthToken(token).build();
        GHRepository repository = gitHub.getRepository("blitzdose/Test");

        return repository.getIssues(GHIssueState.ALL);
    }

    @Override
    protected void onResume() {
        progressIndicator.setVisibility(View.VISIBLE);
        listIssues();
        super.onResume();
    }
}