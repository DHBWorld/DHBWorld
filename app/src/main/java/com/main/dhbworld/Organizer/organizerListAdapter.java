package com.main.dhbworld.Organizer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.main.dhbworld.Dualis.DualisAPI;
import com.main.dhbworld.Events;
import com.main.dhbworld.Organizer.Course;
import com.main.dhbworld.R;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;

public class organizerListAdapter extends ArrayAdapter<Course> {
    public organizerListAdapter(Context context, ArrayList<Course> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View newRow, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (newRow == null) {
            newRow = LayoutInflater.from(getContext()).inflate(R.layout.organizer_entry, parent, false);
        }
        // Lookup view for data population
        TextView tvinfo1 = (TextView) newRow.findViewById(R.id.course_info1);
        TextView tvinfo2 = (TextView) newRow.findViewById(R.id.course_info2);
        // Populate the data into the template view using the data object
        tvinfo1.setText(course.getName());
        tvinfo2.setText(course.getStudy());
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

                bottomSheetDialog.setContentView(R.layout.organizerentrybottomsheet);
                bottomSheetDialog.show();

                try {
                    TextView entryView = bottomSheetDialog.findViewById(R.id.organizerEntryText);
                    Objects.requireNonNull(entryView).setText(course.name);
                    TextView studyView = bottomSheetDialog.findViewById(R.id.organizerStudyText);
                    Objects.requireNonNull(studyView).setText("Studiengang: " + course.study);
                    TextView yearView = bottomSheetDialog.findViewById(R.id.organizerYearText);
                    Objects.requireNonNull(yearView).setText("Jahrgang: " + course.year);
                    TextView roomView = bottomSheetDialog.findViewById(R.id.organizerRoomText);
                    if(course.roomNo != null) {
                        (roomView).setText("Raum: " + course.roomNo);
                    }
                    else{
                        assert roomView != null;
                        (roomView).setVisibility(View.INVISIBLE);
                    }
                    bottomSheetDialog.show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}