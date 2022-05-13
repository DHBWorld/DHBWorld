package com.main.dhbworld.Organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.main.dhbworld.R;

import java.util.ArrayList;

public class OrganizerPersonAdapter extends ArrayAdapter{

        public OrganizerPersonAdapter(Context context, ArrayList<Person> people) {
            super(context, 0, people);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Person person = (Person) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.organizer_entry, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.course_info1);
            TextView tvHome = (TextView) convertView.findViewById(R.id.course_info2);
            // Populate the data into the template view using the data object
            tvName.setText(person.getName());
            tvHome.setText(person.getEmail());
            // Return the completed view to render on screen
            return convertView;
        }
    }
