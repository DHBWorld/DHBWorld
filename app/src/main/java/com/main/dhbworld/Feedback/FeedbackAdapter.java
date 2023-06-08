package com.main.dhbworld.Feedback;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.dhbworld.Feedback.data.FeedbackIssue;
import com.main.dhbworld.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    ArrayList<FeedbackIssue> feedbackArrayList;
    Activity activity;
    String token;

    public FeedbackAdapter(ArrayList<FeedbackIssue> feedbackArrayList, String token, Activity activity) {
        this.feedbackArrayList = feedbackArrayList;
        this.activity = activity;
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
        holder.textViewTitle.setText(feedback.getTitle().substring(0, feedback.getTitle().lastIndexOf("#")-1));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        try {
            holder.textViewUpdated.setText(String.format("Updated: %s", simpleDateFormat.format(feedback.getUpdatedAt())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, DetailFeedbackActivity.class);
            intent.putExtra("issueId", feedback.getId());
            intent.putExtra("token", token);
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

        public ViewHolder(View view) {
            super(view);

            textViewTitle = view.findViewById(R.id.feedback_item_title);
            textViewUpdated = view.findViewById(R.id.feedback_item_updated);
        }
    }
}
