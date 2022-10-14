package com.main.dhbworld.Organizer;
import android.annotation.SuppressLint;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.Objects;

public class OrganizerRoomAdapter extends RecyclerView.Adapter<OrganizerRoomAdapter.ViewHolder>{
    ArrayList<Room> rooms;

    public OrganizerRoomAdapter(ArrayList<Room> rooms) {
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
                    TextView titleRoomUrl = bottomSheetDialog.findViewById(R.id.organizerRoomUrlTitle);
                    if(room.url != null && roomUrl != null) {
                        roomUrl.setText(room.url);
                        titleRoomUrl.setText(R.string.timeplan);

                    }
                    else {
                        Objects.requireNonNull(roomUrl).setVisibility(View.GONE);
                        Objects.requireNonNull(titleRoomUrl).setVisibility(View.GONE);
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