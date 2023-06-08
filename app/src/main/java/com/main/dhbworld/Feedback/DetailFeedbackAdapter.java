package com.main.dhbworld.Feedback;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.Feedback.data.Comment;
import com.main.dhbworld.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailFeedbackAdapter extends RecyclerView.Adapter<DetailFeedbackAdapter.ViewHolder> {

    private final ArrayList<Comment> comments;
    private final Context context;

    public DetailFeedbackAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailFeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_comments_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailFeedbackAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.textViewAuthor.setText(comment.isByMe() ? "Du" : comment.getAuthor());
        holder.textViewBody.setText(comment.getBody());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        holder.textViewDate.setText(simpleDateFormat.format(comment.getTime()));

        if (comment.isByMe()) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            params.setMarginStart(calcDpInPixel(48));
            params.setMarginEnd(calcDpInPixel(4));
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            params.setMarginStart(calcDpInPixel(4));
            params.setMarginEnd(calcDpInPixel(48));
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private int calcDpInPixel(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView textViewAuthor;
        final TextView textViewBody;
        final TextView textViewDate;
        final MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.feedback_comment_author);
            textViewBody = itemView.findViewById(R.id.feedback_comment_body);
            textViewDate = itemView.findViewById(R.id.feedback_comment_updated);
            cardView = itemView.findViewById(R.id.feedback_comment_card);
        }
    }
}
