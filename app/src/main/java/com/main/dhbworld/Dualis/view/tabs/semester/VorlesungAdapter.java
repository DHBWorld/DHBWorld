package com.main.dhbworld.Dualis.view.tabs.semester;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.main.dhbworld.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VorlesungAdapter extends RecyclerView.Adapter<VorlesungAdapter.MyViewHolder> {

    private final List<VorlesungModel> dataModelList;
    private final Context mContext;
    private int mExpandedPosition = -1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView subtitleTextView;
        public TextView notenTextview;
        public TextView creditsTextView;
        public TextView endnoteTextView;
        public ImageView materialButton;
        public LinearLayout layoutGrades;
        public View view;
        public ConstraintLayout expandLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            titleTextView = itemView.findViewById(R.id.card_title);
            creditsTextView = itemView.findViewById(R.id.credits);
            endnoteTextView = itemView.findViewById(R.id.endnote);
            materialButton = itemView.findViewById(R.id.expand_image);
            expandLayout = itemView.findViewById(R.id.expanded_layout);
            layoutGrades = itemView.findViewById(R.id.grades);
        }

        public void bindData(VorlesungModel vorlesungModel) {
            titleTextView.setText(vorlesungModel.getTitle());

            layoutGrades.removeAllViews();

            for (int i=0; i<vorlesungModel.getPruefungen().length(); i++) {
                try {
                    JSONObject pruefung = vorlesungModel.getPruefungen().getJSONObject(i);

                    ConstraintLayout constraintLayout = new ConstraintLayout(itemView.getContext());
                    constraintLayout.setId(View.generateViewId());
                    constraintLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT));

                    TextView grade = new TextView(itemView.getContext(), null, 0, android.R.style.TextAppearance_Material_Caption);
                    grade.setId(View.generateViewId());
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    Resources r = itemView.getContext().getResources();
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            8,
                            r.getDisplayMetrics()
                    );
                    layoutParams.setMargins(0, px, 0, 0);

                    grade.setLayoutParams(layoutParams);

                    TextView subtitle = new TextView(itemView.getContext(), null, 0, android.R.style.TextAppearance_Material_Caption);
                    subtitle.setId(View.generateViewId());
                    subtitle.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT));

                    ConstraintLayout.LayoutParams paramsSubtitle = (ConstraintLayout.LayoutParams) subtitle.getLayoutParams();
                    paramsSubtitle.endToStart = grade.getId();
                    paramsSubtitle.startToStart = constraintLayout.getId();
                    paramsSubtitle.topToTop = constraintLayout.getId();
                    paramsSubtitle.constrainedWidth = true;
                    subtitle.requestLayout();

                    ConstraintLayout.LayoutParams paramsGrade = (ConstraintLayout.LayoutParams) grade.getLayoutParams();
                    paramsGrade.endToEnd = constraintLayout.getId();
                    paramsGrade.bottomToBottom = subtitle.getId();
                    grade.requestLayout();


                    constraintLayout.addView(subtitle);
                    constraintLayout.addView(grade);

                    layoutGrades.addView(constraintLayout);

                    subtitle.setText(pruefung.getString("thema"));
                    grade.setText(pruefung.getString("note"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            creditsTextView.setText(vorlesungModel.getCredits());
            endnoteTextView.setText(vorlesungModel.getEndnote());
        }
    }

    public VorlesungAdapter(List<VorlesungModel> modelList, Context context) {
        dataModelList = modelList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dualis_card_list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(dataModelList.get(position));

        final boolean isExpanded = position == mExpandedPosition;
        holder.expandLayout.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.materialButton.setImageDrawable(isExpanded?AppCompatResources.getDrawable(mContext, R.drawable.ic_baseline_expand_less_24):AppCompatResources.getDrawable(mContext, R.drawable.ic_baseline_expand_more_24));
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1:position;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }
}