package com.main.dhbworld.Feedback.data;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class FeedbackIssue {
    private final GHIssue ghIssue;

    public FeedbackIssue(GHIssue ghIssue) {
        this.ghIssue = ghIssue;
    }

    public String getTitle() {
        return ghIssue.getTitle();
    }

    public Date getUpdatedAt() throws IOException {
        return ghIssue.getUpdatedAt();
    }

    public int getId() {
        return ghIssue.getNumber();
    }

    public Collection<GHLabel> getLabels() {
        return ghIssue.getLabels();
    }

    public int getCommentCount() {
        return ghIssue.getCommentsCount();
    }
}
