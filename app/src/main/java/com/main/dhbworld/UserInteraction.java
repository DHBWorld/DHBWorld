package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.dhbworld.Enums.InteractionState;

import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;

import java.util.ArrayList;

public class UserInteraction extends AppCompatActivity {
    private ArrayList <Button> ja;
    private ArrayList <Button>  nein;

    private TextView zustandKantineTV;
    private TextView zustandKaffeeTV;
    private TextView zustandDrukerTV;
    private TextView meldungenKantineTV;
    private TextView meldungenKaffeeTV;
    private TextView meldungenDrukerTV;

    private InteractionState zustandKantine;
    private InteractionState zustandKaffee;
    private InteractionState zustandDruker;

    private int meldungenKantine;
    private int meldungenKaffee;
    private int meldungenDruker;

    private LinearLayout imageBox_kantine;
    private LinearLayout imageBox_kaffee;
    private LinearLayout imageBox_drucker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interaction_layout);

        zustandManagement();
        jaNeinButtonsManagement();
        meldungenManagement();



    }

    private void jaNeinButtonsManagement(){
        ja =new ArrayList<> ();
        ja.add(findViewById(R.id.ja0));
        ja.add(findViewById(R.id.ja1));
        ja.add(findViewById(R.id.ja2));


        nein =new ArrayList<> ();
        nein.add(findViewById(R.id.nein0));
        nein.add(findViewById(R.id.nein1));
        nein.add(findViewById(R.id.nein2));

        String[][] states= new String[3][];
        states[0]= new String[]{InteractionState.QUEUE_KURZ.getText(), InteractionState.QUEUE_MIDDLE.getText(), InteractionState.QUEUE_LONG.getText()};
        states[1]= new String[]{InteractionState.CLEANING.getText(), InteractionState.DEFECT.getText()};
        states[2]= new String[]{"", ""};

        for(Button j:ja){
            j.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, states[ja.indexOf(j)] , R.string.problem_melden);
                    confirmation.show();

                    //Backend logik: Ein Event wurde gemeldet

                }});

        }

        ja.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_melden_dass_der_drucker_kaputt_ist, R.string.problem_melden);
                confirmation.show();

                //Backend logik: Ein Event wurde gemeldet
            }
        });



        for(Button n:nein){
            n.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_das_problem_besteht_nicht_mehr, R.string.problem_ist_geloest);
                    confirmation.show();


                    //Backend logik: Das Event besteht nicht mehr

                }});}

    }

    private void zustandManagement(){

        zustandKantineTV= findViewById(R.id.zustand_kantine);
        zustandKaffeeTV= findViewById(R.id.zustand_kaffee);
        zustandDrukerTV= findViewById(R.id.zustand_druker);

        zustandKantine=InteractionState.QUEUE_ABCENT;
        zustandKaffee=InteractionState.NORMAL;
        zustandDruker=InteractionState.NORMAL;

        zustandKantineTV.setText(getResources().getString(R.string.zustand)+" "+zustandKantine.getText());
        zustandKaffeeTV.setText(getResources().getString(R.string.zustand)+" "+zustandKaffee.getText());
        zustandDrukerTV.setText(getResources().getString(R.string.zustand)+" "+zustandDruker.getText());

        Resources res = getResources();

        imageBox_kantine= findViewById(R.id.imageBox_kantine);
        imageBox_kaffee= findViewById(R.id.imageBox_kaffee);
        imageBox_drucker= findViewById(R.id.imageBox_drucker);

        imageBox_kantine.setBackgroundColor(res.getColor(zustandKantine.getColor()));
        imageBox_kaffee.setBackgroundColor(res.getColor(zustandKaffee.getColor()));
        imageBox_drucker.setBackgroundColor(res.getColor(zustandDruker.getColor()));
    }

    private void meldungenManagement(){
        meldungenKantineTV= findViewById(R.id.bisherige_meldungen_kantine);
        meldungenKaffeeTV= findViewById(R.id.bisherige_meldungen_kaffe);
        meldungenDrukerTV= findViewById(R.id.bisherige_meldungen_druker);

        meldungenKantine=0;
        meldungenKaffee=1;
        meldungenDruker=3;

        meldungenKantineTV.setText(getResources().getString(R.string.bisherigen_meldungen)+" "+Integer.toString(meldungenKantine));
        meldungenKaffeeTV.setText(getResources().getString(R.string.bisherigen_meldungen)+" "+Integer.toString(meldungenKaffee));
        meldungenDrukerTV.setText(getResources().getString(R.string.bisherigen_meldungen)+" "+Integer.toString(meldungenDruker));

    }

    public InteractionState getZustandKantine() {
        return zustandKantine;
    }

    public void setZustandKantine(InteractionState zustandKantine) {
        this.zustandKantine = zustandKantine;
    }

    public InteractionState getZustandKaffee() {
        return zustandKaffee;
    }

    public void setZustandKaffee(InteractionState zustandKaffee) {
        this.zustandKaffee = zustandKaffee;
    }

    public InteractionState getZustandDruker() {
        return zustandDruker;
    }

    public void setZustandDruker(InteractionState zustandDruker) {
        this.zustandDruker = zustandDruker;
    }

    public int getMeldungenKantine() {
        return meldungenKantine;
    }

    public void setMeldungenKantine(int meldungenKantine) {
        this.meldungenKantine = meldungenKantine;
    }

    public int getMeldungenKaffee() {
        return meldungenKaffee;
    }

    public void setMeldungenKaffee(int meldungenKaffee) {
        this.meldungenKaffee = meldungenKaffee;
    }

    public int getMeldungenDruker() {
        return meldungenDruker;
    }

    public void setMeldungenDruker(int meldungenDruker) {
        this.meldungenDruker = meldungenDruker;
    }
}