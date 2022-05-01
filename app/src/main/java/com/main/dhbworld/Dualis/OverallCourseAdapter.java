package com.main.dhbworld.Dualis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.main.dhbworld.KVV.KVVListAdapter;
import com.main.dhbworld.R;

import java.util.ArrayList;

class OverallCourseAdapter extends RecyclerView.Adapter<OverallCourseAdapter.ViewHolder> {

   private Context context;
   private ArrayList<OverallCourseModel> overallCourseModels;

   OverallCourseAdapter(Context context, ArrayList<OverallCourseModel> overallCourseModel) {
      this.context = context;
      this.overallCourseModels = overallCourseModel;
   }

   @NonNull
   @Override
   public OverallCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dualis_overall_course_item, parent, false);
      return new OverallCourseAdapter.ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull OverallCourseAdapter.ViewHolder holder, int position) {
      holder.textViewModuleName.setText(overallCourseModels.get(position).getModuleName());
      holder.textViewCredits.setText(overallCourseModels.get(position).getCredits());
      holder.textViewGrade.setText(overallCourseModels.get(position).getGrade());
      if (overallCourseModels.get(position).isPassed()) {
         holder.imageViewPassed.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_check_box_24));
      } else {
         holder.imageViewPassed.setImageDrawable(null);
      }
   }

   @Override
   public int getItemCount() {
      return overallCourseModels.size();
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      final TextView textViewModuleName;
      final TextView textViewCredits;
      final TextView textViewGrade;
      final ImageView imageViewPassed;

      public ViewHolder(View view) {
         super(view);

         textViewModuleName = view.findViewById(R.id.module_name_tv);
         textViewCredits = view.findViewById(R.id.credits_tv);
         textViewGrade = view.findViewById(R.id.grade_tv);
         imageViewPassed = view.findViewById(R.id.passed_iv);
      }
   }
}
