package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class PersonalInformationActivity extends AppCompatActivity implements View.OnClickListener{
    FloatingActionButton editButton;
    Button saveButton;
    Button cancelButton;
    TextView name;
    TextView mNumber;
    TextView lNumber;
    TextView studentMail;
    TextView freeNotes;

    SharedPreferences sp;
    public static final String MyPREFERENCES = "" ;
    public static final String Name = "";
    public static final String LibraryNumber = "";
    public static final String MatriculationNumber = "";
    public static final String  StudentMail= "";
    public static final String FreeNotes = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_information);

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


        TextInputLayout textInputLayout_name= findViewById(R.id.textInputLayoutPI_name);
        textInputLayout_name.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", name.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PersonalInformationActivity.this, "Copy", Toast.LENGTH_LONG).show();
            }
        });


        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);




        editButton.setOnClickListener(v -> {
            Toast.makeText(PersonalInformationActivity.this,
                    "R.string.error_getting_kvv_data",
                    Toast.LENGTH_LONG
            ).show();
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

            String n = name.getText().toString();
            String m = mNumber.getText().toString();
            String l = lNumber.getText().toString();
            String s = studentMail.getText().toString();
            String f = freeNotes.getText().toString();

            SharedPreferences.Editor editor = sp.edit();

            editor.putString(Name,n);
            editor.putString(MatriculationNumber,m);
            editor.putString(LibraryNumber,l);
            editor.putString(StudentMail,s);
            editor.putString(FreeNotes,f);
            editor.apply();



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
