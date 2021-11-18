package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;

public class UserInteraction extends AppCompatActivity {
    private Button[] ja;
    private Button[] nein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interaction_layout);
        ja =new Button[3];
        ja[0]= findViewById(R.id.ja0);
        ja[1]= findViewById(R.id.ja1);
        ja[2]= findViewById(R.id.ja2);

        nein =new Button[3];
        nein[0]= findViewById(R.id.nein0);
        nein[1]= findViewById(R.id.nein1);
        nein[2]= findViewById(R.id.nein2);

        for(Button j:ja){
            j.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_event_melden);
                    confirmation.show();

                    //Backend logik: Ein Event wurde gemeldet

                }});}



        for(Button n:nein){
            n.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_das_problem_besteht_nicht_mehr);
                    confirmation.show();

                    //Backend logik: Das Event besteht nicht mehr

                }});}


    }
}