package com.main.dhbworld.Organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.main.dhbworld.R;

import java.util.ArrayList;

public class OrganizerRoomAdapter extends ArrayAdapter {
    public OrganizerRoomAdapter(Context context, ArrayList<Room> rooms) {
        super(context, 0, rooms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Room room = (Room) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.organizer_entry, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.course_info1);
        TextView tvHome = (TextView) convertView.findViewById(R.id.course_info2);
        // Populate the data into the template view using the data object
        tvName.setText(room.getName());
        tvHome.setText(room.getRoomType());
        // Return the completed view to render on screen
        return convertView;
    }
}
