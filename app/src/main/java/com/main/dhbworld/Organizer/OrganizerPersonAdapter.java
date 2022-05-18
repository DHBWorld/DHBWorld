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

public class OrganizerPersonAdapter extends RecyclerView.Adapter<OrganizerPersonAdapter.ViewHolder>{
    ArrayList<Person> people;


    public OrganizerPersonAdapter(ArrayList<Person> people) {
        this.people = people;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is to inflate our layout.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerPersonAdapter.ViewHolder holder, int position) {
        // setting data to our views of recycler view.
        Person person = people.get(position);
        holder.tvName.setText(person.getName());
        holder.tvHome.setText(person.getStudy());
        // Return the completed view to render on screen
        setOnClickListener(holder.itemView,person);
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return people.size();
    }

    public Object getItem(int position) {
        return people.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setOnClickListener(View newRow, Person person){
        newRow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(newRow.getContext());

                bottomSheetDialog.setContentView(R.layout.organizerpersonbottomsheet);
                bottomSheetDialog.show();

                try {
                    TextView personNameText = bottomSheetDialog.findViewById(R.id.organizerPersonNameText);
                    Objects.requireNonNull(personNameText).setText(person.name);
                    TextView personStudyText = bottomSheetDialog.findViewById(R.id.organizerPersonStudyText);
                    if(person.study != null && personStudyText != null) {
                        personStudyText.setText(person.study);
                    }
                    Objects.requireNonNull(personStudyText).setText("Study: " + person.study);
                    TextView personEmailText = bottomSheetDialog.findViewById(R.id.organizerPersonEmailText);
                    Objects.requireNonNull(personEmailText).setText("Mail: " + person.email);
                    TextView personPhoneText = bottomSheetDialog.findViewById(R.id.organizerPersonPhoneText);
                    Objects.requireNonNull(personPhoneText).setText("Phone: " + person.phoneNumber);
                    TextView personRoomText = bottomSheetDialog.findViewById(R.id.organizerPersonRoomText);
                    Objects.requireNonNull(personRoomText).setText("Room: " + person.roomNo);


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