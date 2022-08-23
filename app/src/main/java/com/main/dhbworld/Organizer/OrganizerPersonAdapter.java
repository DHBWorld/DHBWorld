package com.main.dhbworld.Organizer;
import android.annotation.SuppressLint;
import android.content.Context;
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
    Context context;


    public OrganizerPersonAdapter(Context context, ArrayList<Person> people) {
        this.context = context;
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
                    if(context.getString(R.string.study, person.study) != null && !context.getString(R.string.study, person.study).contains("null")){
                        System.out.println(context.getString(R.string.study, person.study));
                        Objects.requireNonNull(personStudyText).setText(context.getString(R.string.study, person.study));}
                    else{
                        Objects.requireNonNull(personStudyText).setVisibility(View.GONE);}
                    TextView personEmailText = bottomSheetDialog.findViewById(R.id.organizerPersonEmailText);
                    if(context.getString(R.string.mail, person.email) != null && context.getString(R.string.mail, person.email).contains("@")){
                    Objects.requireNonNull(personEmailText).setText(context.getString(R.string.mail, person.email));}
                    else{
                        Objects.requireNonNull(personEmailText).setVisibility(View.GONE); }
                    TextView personPhoneText = bottomSheetDialog.findViewById(R.id.organizerPersonPhoneText);
                    if(context.getString(R.string.phone, person.phoneNumber) != null && context.getString(R.string.phone, person.phoneNumber).contains("+")){
                        Objects.requireNonNull(personPhoneText).setText(context.getString(R.string.phone, person.phoneNumber));}
                    else{
                        Objects.requireNonNull(personPhoneText).setVisibility(View.GONE); }
                    TextView personRoomText = bottomSheetDialog.findViewById(R.id.organizerPersonRoomText);
                    if(context.getResources().getString(R.string.room_placeholder, person.roomNo) != null && !context.getResources().getString(R.string.room_placeholder, person.roomNo).equalsIgnoreCase("")){
                        Objects.requireNonNull(personRoomText).setText(context.getResources().getString(R.string.room_placeholder, person.roomNo));}
                    else{
                        Objects.requireNonNull(personEmailText).setVisibility(View.GONE); }
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