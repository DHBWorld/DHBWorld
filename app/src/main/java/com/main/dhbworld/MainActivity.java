package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


        FloatingActionButton editButton;
        Button saveButton;
        Button cancelButton;
        TextView name;
        TextView mNumber;
        TextView lNumber;
        TextView studentMail;
        TextView freeNotes;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editButton = findViewById(R.id.edit_Button);
        saveButton = findViewById(R.id.save_Button);
        cancelButton = findViewById(R.id.cancel_Button);
        name = findViewById(R.id.studentName);
        mNumber = findViewById(R.id.matriculationNumber);
        lNumber = findViewById(R.id.libraryNumber);
        studentMail = findViewById(R.id.studentMail);
        freeNotes = findViewById(R.id.freeNotes);

        name.setEnabled(false);
        mNumber.setEnabled(false);
        lNumber.setEnabled(false);
        studentMail.setEnabled(false);
        freeNotes.setEnabled(false);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });



        editButton.setOnClickListener(v -> {
            editButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

            name.setEnabled(true);
            mNumber.setEnabled(true);
            lNumber.setEnabled(true);
            studentMail.setEnabled(true);
            freeNotes.setEnabled(true);
        });

        saveButton.setOnClickListener(v -> {
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);

            name.setEnabled(false);
            mNumber.setEnabled(false);
            lNumber.setEnabled(false);
            studentMail.setEnabled(false);
            freeNotes.setEnabled(false);
        });

        cancelButton.setOnClickListener(v -> {
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);

            name.setEnabled(false);
            mNumber.setEnabled(false);
            lNumber.setEnabled(false);
            studentMail.setEnabled(false);
            freeNotes.setEnabled(false);
        });
    }



    @Override
    public void onClick(View v) {

    }
}