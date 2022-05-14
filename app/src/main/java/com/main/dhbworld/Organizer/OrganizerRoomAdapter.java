package com.main.dhbworld.Organizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.Objects;

public class OrganizerRoomAdapter extends ArrayAdapter {
    public OrganizerRoomAdapter(Context context, ArrayList<Room> rooms) {
        super(context, 0, rooms);
    }

    @Override
    public View getView(int position, View newRow, ViewGroup parent) {
        // Get the data item for this position
        Room room = (Room) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (newRow == null) {
            newRow = LayoutInflater.from(getContext()).inflate(R.layout.organizer_entry, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) newRow.findViewById(R.id.info_box1);
        TextView tvHome = (TextView) newRow.findViewById(R.id.info_box2);
        // Populate the data into the template view using the data object
        tvName.setText(room.getName());
        tvHome.setText(room.getRoomType());
        // Return the completed view to render on screen
        setOnClickListener(newRow,room);
        return newRow;
    }

    public void setOnClickListener(View newRow, Room room){
        newRow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.organizerroombottomsheet);
                bottomSheetDialog.show();
                try {
                    TextView roomName = bottomSheetDialog.findViewById(R.id.organizerRoomNameText);
                    Objects.requireNonNull(roomName).setText(room.name);
                    TextView roomType = bottomSheetDialog.findViewById(R.id.organizerRoomTypeText);
                    Objects.requireNonNull(roomType).setText(room.roomType);
                    LinearLayout roomUrlView  = bottomSheetDialog.findViewById(R.id.organizerRoomUrlView);
                    TextView roomUrl = bottomSheetDialog.findViewById(R.id.organizerRoomUrlText);
                    if(room.url != null && roomUrl != null) {
                        roomUrl.setText(room.url);
                    }
                    else{
                        Objects.requireNonNull(roomUrlView).setVisibility(View.GONE);
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
