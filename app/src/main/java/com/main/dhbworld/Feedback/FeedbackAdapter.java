package com.main.dhbworld.Feedback;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.main.dhbworld.Feedback.data.FeedbackIssue;
import com.main.dhbworld.R;

import org.kohsuke.github.GHLabel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    ArrayList<FeedbackIssue> feedbackArrayList;
    Activity activity;

    String repo;
    String token;

    public FeedbackAdapter(ArrayList<FeedbackIssue> feedbackArrayList, String token, String repo, Activity activity) {
        this.feedbackArrayList = feedbackArrayList;
        this.activity = activity;
        this.repo = repo;
        this.token = token;
    }

    @NonNull
    @Override
    public FeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.ViewHolder holder, int position) {
        FeedbackIssue feedback = feedbackArrayList.get(position);
        holder.textViewTitle.setText(String.format(Locale.getDefault(), "#%d %s", feedback.getId(), feedback.getTitle().substring(0, feedback.getTitle().lastIndexOf("#") - 1)));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        try {
            holder.textViewUpdated.setText(String.format("Updated: %s", simpleDateFormat.format(feedback.getUpdatedAt())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (GHLabel ghLabel : feedback.getLabels()) {
            Chip chip = new Chip(activity);
            chip.setText(ghLabel.getName());
            chip.setClickable(false);
            holder.chipGroupLabels.addView(chip);
        }

        holder.chipGroupLabels.setOnClickListener(v -> holder.itemView.performClick());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();

        int savedCommentCount = preferences.getInt("feedback_commentcount_" + feedback.getId(), 0);
        if (savedCommentCount < feedback.getCommentCount()) {
            holder.imageViewNewComments.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewNewComments.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            editor.putInt("feedback_commentcount_" + feedback.getId(), feedback.getCommentCount());
            editor.apply();

            Intent intent = new Intent(activity, DetailFeedbackActivity.class);
            intent.putExtra("issueId", feedback.getId());
            intent.putExtra("token", token);
            intent.putExtra("repo", repo);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return feedbackArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewTitle;
        final TextView textViewUpdated;
        final ImageView imageViewNewComments;
        final ChipGroup chipGroupLabels;

        public ViewHolder(View view) {
            super(view);

            textViewTitle = view.findViewById(R.id.feedback_item_title);
            textViewUpdated = view.findViewById(R.id.feedback_item_updated);
            imageViewNewComments = view.findViewById(R.id.feedback_item_new_comments);
            chipGroupLabels = view.findViewById(R.id.feedback_item_label_group);
        }
    }
}
