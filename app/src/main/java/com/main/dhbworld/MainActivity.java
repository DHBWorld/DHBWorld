package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Firebase.CurrentStatusListener;
import com.main.dhbworld.Firebase.DataSendListener;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.R;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interaction_layout);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Bitte warten");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        Utilities.subscribeToTopic();
        Utilities utilities = new Utilities(this);
        utilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                firebaseUser = user;
                dialog.dismiss();
            }

            @Override
            public void onSignInError() {

            }
        });
        utilities.signIn();

        utilities.setDataSendListener(new DataSendListener() {
            @Override
            public void success() {
                Toast.makeText(getApplicationContext(), "SEND SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                utilities.getCurrentStatus(Utilities.CATEGORY_COFFEE);
            }

            @Override
            public void failed(Exception e) {
                Toast.makeText(getApplicationContext(), "SEND FAILED: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        utilities.setCurrentStatusListener(new CurrentStatusListener() {
            @Override
            public void onStatusReceived(String category, int status) {

            }
        });

        MaterialButton button = findViewById(R.id.cantine_yes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilities.addToDatabase(Utilities.CATEGORY_COFFEE, Utilities.PROBLEM_CLEANING);
            }
        });

        MaterialButton button2 = findViewById(R.id.cantine_no);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilities.addToDatabase(Utilities.CATEGORY_COFFEE, Utilities.PROBLEM_FUNCTIONING);
            }
        });
    }
}