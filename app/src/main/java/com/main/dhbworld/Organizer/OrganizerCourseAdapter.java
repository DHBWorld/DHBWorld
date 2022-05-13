package com.main.dhbworld.Organizer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.main.dhbworld.R;

import java.util.ArrayList;

public class OrganizerCourseAdapter extends ArrayAdapter{
    ArrayList<> currentList = new ArrayList();

    public OrganizerCourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.organizer_entry, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.course_info1);
        TextView tvHome = (TextView) convertView.findViewById(R.id.course_info2);
        // Populate the data into the template view using the data object
        tvName.setText(course.getName());
        tvHome.setText(course.getStudy());
        // Return the completed view to render on screen
        return convertView;
    }
}