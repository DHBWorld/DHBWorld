package com.main.dhbworld.Feedback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.main.dhbworld.Feedback.data.Comment;
import com.main.dhbworld.R;
import com.main.dhbworld.Thread.ThreadWithUiUpdate;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.ArrayList;

public class DetailFeedbackActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private RecyclerView recyclerView;

    private AppCompatEditText commentMessageEditText;
    private MaterialCardView sendButton;

    private String repo;
    private GHIssue issue;

    private ArrayList<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feedback);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        int issueId = bundle.getInt("issueId");
        String token = bundle.getString("token");
        repo = bundle.getString("repo");

        setupViews();

        new ThreadWithUiUpdate(() -> {
            try {
                initializeGitHub(token, issueId);
                comments = getComments();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).afterOnUiThread(this, () -> {
            progressIndicator.setVisibility(View.GONE);
            recyclerView.setAdapter(new DetailFeedbackAdapter(comments, DetailFeedbackActivity.this));
            recyclerView.scrollToPosition(Math.max(0, comments.size()-1));
        }).start();

        sendButton.setOnClickListener(v -> {
            Editable messageText = commentMessageEditText.getText();
            if (messageText == null) {
                return;
            }
            String message = messageText.toString();
            if (message.isEmpty()) {
                return;
            }

            sendButton.setEnabled(false);
            progressIndicator.setVisibility(View.VISIBLE);

            sendComment(message, () -> {
                commentMessageEditText.setText(null);
                updateComments();
                sendButton.setEnabled(true);
            });
        });
    }

    private void sendComment(String message, Runnable after) {
        new ThreadWithUiUpdate(() -> {
            try {
                issue.comment(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).afterOnUiThread(this, after).start();
    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressIndicator = findViewById(R.id.feedback_detail_activity_progressindicator);
        recyclerView = findViewById(R.id.feedback_detail_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        commentMessageEditText = findViewById(R.id.commend_message);
        sendButton = findViewById(R.id.comment_send_button);
    }

    private void initializeGitHub(String token, int issueId) throws IOException {
        GitHub gitHub = new GitHubBuilder().withOAuthToken(token).build();
        GHRepository repository = gitHub.getRepository(repo);
        issue = repository.getIssue(issueId);
    }

    private ArrayList<Comment> getComments() throws IOException {
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment(issue.getUser().getLogin(), issue.getCreatedAt(),issue.getBody()));

        for (GHIssueComment comment : issue.getComments()) {
            comments.add(new Comment(comment.getUser().getLogin(), comment.getUpdatedAt(), comment.getBody()));
        }
        return comments;
    }

    private void updateComments() {
        new ThreadWithUiUpdate(() -> {
            comments.clear();
            try {
                comments.addAll(getComments());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).afterOnUiThread(this, () -> {
            progressIndicator.setVisibility(View.GONE);
            recyclerView.smoothScrollToPosition(Math.max(0, comments.size()-1));
        }).start();

    }
}