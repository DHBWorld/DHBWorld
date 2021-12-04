package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
        FloatingActionButton editButton;
        MaterialButton saveButton;
        MaterialButton cancelButton;
        TextView name;
        TextView mNumber;
        TextView lNumber;
        TextView studentMail;
        TextView freeNotes;
        SharedPreferences sp;

        public static final String MyPREFERENCES = "myPreferencesKey" ;
        public static final String Name = "nameKey";
        public static final String LibraryNumber = "libraryNumberKey";
        public static final String MatriculationNumber = "matriculationNumberKey";
        public static final String StudentMail= "studentMailKey";
        public static final String FreeNotes = "freeNotesKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //instantiate buttons, text fields
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
        //when the app is opened it loads the data if there is any
        loadAndUpdateData();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        editButton.setOnClickListener(v -> {
            //visibility off buttons
            editButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            //enabling text field
            name.setEnabled(true);
            mNumber.setEnabled(true);
            lNumber.setEnabled(true);
            studentMail.setEnabled(true);
            freeNotes.setEnabled(true);

        });

        saveButton.setOnClickListener(v -> {
            //visibility off buttons
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            //disabling text field
            name.setEnabled(false);
            mNumber.setEnabled(false);
            lNumber.setEnabled(false);
            studentMail.setEnabled(false);
            freeNotes.setEnabled(false);

            //check MNumber and LNumber if they are too long
            if(checkLNumber(lNumber.getText().toString()) && checkMNumber(mNumber.getText().toString())){
                saveData();
            }else{
                //loadandupdate to set the text fields to the status before. then show a msg to the user
                loadAndUpdateData();
                Toast.makeText(this, "Data could not be saved", Toast.LENGTH_SHORT).show();
            }


        });

        cancelButton.setOnClickListener(v -> {
            //visibility off buttons
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            //disabling text field
            name.setEnabled(false);
            mNumber.setEnabled(false);
            lNumber.setEnabled(false);
            studentMail.setEnabled(false);
            freeNotes.setEnabled(false);

            loadAndUpdateData();
        });


    }





    @Override
    public void onClick(View v) {

    }


    public boolean checkLNumber(String l){
        if(l.isEmpty()){
            return true;
        }else{
            try{
                long libraryNumberLength = Long.parseLong(l);
                if(libraryNumberLength>999999999999l){
                    Toast.makeText(this, "[Bibliotheksnummer] should only contain 12 digits", Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    return true;
                }
            }catch(NumberFormatException g){
                Toast.makeText(this, "[Bibliotheksnummer] does not contain only digits", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public boolean checkMNumber(String m){
        if(m.isEmpty()){
            return true;
        }else{
            try{
                int matriculationNumberLength = Integer.parseInt(m);
                if(matriculationNumberLength>9999999){
                    Toast.makeText(this, "[Matrikelnummer] should only contain 7 digits", Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    return true;
                }
            }catch (NumberFormatException g){
                Toast.makeText(this, "[Matrikelnummer] does not contain only digits", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


    }

    public void saveData(){
        //instantiate the SharedPreferences
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //put the data into it
        editor.putString(Name, name.getText().toString());
        editor.putString(LibraryNumber, lNumber.getText().toString());
        editor.putString(MatriculationNumber, mNumber.getText().toString());
        editor.putString(StudentMail, studentMail.getText().toString());
        editor.putString(FreeNotes, freeNotes.getText().toString());
        editor.apply();
        //show a message to the user
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadAndUpdateData(){
        //load the data and put it into the text field
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        name.setText(sp.getString(Name, ""));
        mNumber.setText(sp.getString(MatriculationNumber,""));
        lNumber.setText(sp.getString(LibraryNumber, ""));
        studentMail.setText(sp.getString(StudentMail,""));
        freeNotes.setText(sp.getString(FreeNotes,""));
    }
}
