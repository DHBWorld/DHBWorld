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

public class OrganizerPersonAdapter extends ArrayAdapter{

        public OrganizerPersonAdapter(Context context, ArrayList<Person> people) {
            super(context, 0, people);
        }

        @Override
        public View getView(int position, View newRow, ViewGroup parent) {
            // Get the data item for this position
            Person person = (Person) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (newRow == null) {
                newRow = LayoutInflater.from(getContext()).inflate(R.layout.organizer_entry, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) newRow.findViewById(R.id.info_box1);
            TextView tvHome = (TextView) newRow.findViewById(R.id.info_box2);
            // Populate the data into the template view using the data object
            tvName.setText(person.getName());
            tvHome.setText(person.getEmail());
            // Return the completed view to render on screen
            setOnClickListener(newRow,person);
            return newRow;
        }

    public void setOnClickListener(View newRow, Person person) {
        newRow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

                bottomSheetDialog.setContentView(R.layout.organizerpersonbottomsheet);
                bottomSheetDialog.show();

                try {
                    TextView personNameText = bottomSheetDialog.findViewById(R.id.organizerPersonNameText);
                    Objects.requireNonNull(personNameText).setText(person.name);
                    TextView personStudyText = bottomSheetDialog.findViewById(R.id.organizerPersonStudyText);
                    LinearLayout personStudyView = bottomSheetDialog.findViewById(R.id.organizerPersonStudyView);
                    if(person.study != null && personStudyText != null) {
                        personStudyText.setText(person.study);
                    }
                    else{
                        Objects.requireNonNull(personStudyView).setVisibility(View.GONE);
                    }
                    Objects.requireNonNull(personStudyText).setText("Study: " + person.study);
                    TextView personEmailText = bottomSheetDialog.findViewById(R.id.organizerPersonEmailText);
                    Objects.requireNonNull(personEmailText).setText("Mail: " + person.email);
                    TextView personPhoneText = bottomSheetDialog.findViewById(R.id.organizerPersonPhoneText);
                    Objects.requireNonNull(personPhoneText).setText("Phone: " + person.phoneNumber);
                    TextView personRoomText = bottomSheetDialog.findViewById(R.id.organizerPersonRoomText);
                    Objects.requireNonNull(personRoomText).setText("Room: " + person.roomNo);

                    bottomSheetDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    }
