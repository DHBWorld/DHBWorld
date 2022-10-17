package com.main.dhbworld.Organizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.main.dhbworld.MapActivity;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.Objects;

public class OrganizerRoomAdapter extends RecyclerView.Adapter<OrganizerRoomAdapter.ViewHolder>{
    ArrayList<Room> rooms;
    Activity activity;

    public OrganizerRoomAdapter(Activity activity, ArrayList<Room> rooms) {
        this.activity = activity;
        this.rooms = rooms;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is to inflate our layout.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerRoomAdapter.ViewHolder holder, int position) {
        // setting data to our views of recycler view.
        Room room = rooms.get(position);
        holder.tvName.setText(room.getName());
        holder.tvHome.setText(room.getRoomType());
        // Return the completed view to render on screen
        setOnClickListener(holder.itemView,room);
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return rooms.size();
    }

    public Object getItem(int position) {
        return rooms.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setOnClickListener(View newRow, Room room){
        newRow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(newRow.getContext());
                bottomSheetDialog.setContentView(R.layout.organizerroombottomsheet);
                bottomSheetDialog.show();

                try {
                    TextView roomName = bottomSheetDialog.findViewById(R.id.organizerRoomNameText);
                    Objects.requireNonNull(roomName).setText(room.name);
                    TextView roomType = bottomSheetDialog.findViewById(R.id.organizerRoomTypeText);
                    Objects.requireNonNull(roomType).setText(room.roomType);
                    TextView roomUrl = bottomSheetDialog.findViewById(R.id.organizerRoomUrlText);
                    if(room.url != null && roomUrl != null) {
                        roomUrl.setText(room.url);
                    }
                    else {
                        Objects.requireNonNull(roomUrl).setVisibility(View.GONE);
                    }

                    Button button = bottomSheetDialog.findViewById(R.id.find_room_button);
                    if (button != null) {
                        button.setOnClickListener(view -> {
                            Intent intent = new Intent(activity, MapActivity.class);
                            intent.putExtra("room", room.name);
                            activity.startActivity(intent);
                        });
                    }
                    bottomSheetDialog.show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views.
        private final TextView tvName;
        private final TextView tvHome;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            tvName = itemView.findViewById(R.id.info_box1);
            tvHome = itemView.findViewById(R.id.info_box2);

        }
    }
}