package com.main.dhbworld.Organizer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrganizerCourseAdapter extends ArrayAdapter implements Filterable {
    ArrayList<Course> courses;
    ArrayList<Course> filteredData;

    public OrganizerCourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
        this.courses = courses;
        this.filteredData = courses;
    }


    @Override
    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View newRow, ViewGroup parent) {
        // Get the data item for this position
        Course course = (Course) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (newRow == null) {
            newRow = LayoutInflater.from(getContext()).inflate(R.layout.organizer_entry, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) newRow.findViewById(R.id.info_box1);
        TextView tvHome = (TextView) newRow.findViewById(R.id.info_box2);
        // Populate the data into the template view using the data object
        tvName.setText(course.getName());
        tvHome.setText(course.getStudy());
        // Return the completed view to render on screen
        setOnClickListener(newRow,course);
        return newRow;
    }

    public void setOnClickListener(View newRow, Course course){
        newRow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

                bottomSheetDialog.setContentView(R.layout.organizercoursebottomsheet);
                bottomSheetDialog.show();

                try {
                    TextView entryView = bottomSheetDialog.findViewById(R.id.organizerCourseEntryText);
                    Objects.requireNonNull(entryView).setText(course.name);
                    TextView studyView = bottomSheetDialog.findViewById(R.id.organizerCourseStudyText);
                    Objects.requireNonNull(studyView).setText("Study: " + course.study);
                    TextView yearView = bottomSheetDialog.findViewById(R.id.organizerCourseYearText);
                    Objects.requireNonNull(yearView).setText("Year: " + course.year);
                    TextView roomView = bottomSheetDialog.findViewById(R.id.organizerCourseRoomText);
                    if(course.roomNo != null && roomView != null) {
                        (roomView).setText("Room: " + course.roomNo);
                    }
                    else{
                        assert roomView != null;
                        (roomView).setVisibility(View.GONE);
                    }
                    TextView urlText = bottomSheetDialog.findViewById(R.id.organizerCourseUrlText);
                    if(course.url != null && urlText != null) {
                        (urlText).setText("URL: " + Html.fromHtml(course.url,0));
                        urlText.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    else{
                        (roomView).setVisibility(View.GONE);
                    }


                    bottomSheetDialog.show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public Filter getFilter(){
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if (constraint.toString().length() > 0) {
                   final ArrayList<Course> nList = new ArrayList<>();
                    for(Course item: filteredData ){
                        if(item.filterString().toLowerCase().contains(constraint)){
                            nList.add(item);
                        }
                    }
                    result.values = nList;
                    result.count = nList.size();
                }else {
                    result.values = courses;
                    result.count = courses.size();
                }
                return result;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                filteredData.clear();
                filteredData.addAll((ArrayList<Course>)filterResults.values);
                System.out.println(filteredData);
                notifyDataSetChanged();
            }

        };
    }
}