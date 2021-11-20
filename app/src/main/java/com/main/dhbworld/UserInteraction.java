package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;

public class UserInteraction extends AppCompatActivity {
    private Button[] ja;
    private Button[] nein;

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

    private ImageView bildKantine;
    private ImageView bildKaffee;
    private ImageView bildDruker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interaction_layout);

        zustandManagement();
        jaNeinButtonsManagement();
        meldungenManagement();



    }

    private void jaNeinButtonsManagement(){
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
                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_event_melden, R.string.problem_melden);
                    confirmation.show();

                    //Backend logik: Ein Event wurde gemeldet

                }});}



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
        zustandKaffee=InteractionState.DEFECT;
        zustandDruker=InteractionState.NORMAL;

        zustandKantineTV.setText(getResources().getString(R.string.zustand)+" "+zustandKantine.getText());
        zustandKaffeeTV.setText(getResources().getString(R.string.zustand)+" "+zustandKaffee.getText());
        zustandDrukerTV.setText(getResources().getString(R.string.zustand)+" "+zustandDruker.getText());

        Resources res = getResources();
        bildKantine=  findViewById(R.id.bild_kantine);
        bildKaffee=  findViewById(R.id.bild_kaffee);
        bildDruker=  findViewById(R.id.bild_druker);

        bildKantine.setColorFilter(res.getColor(zustandKantine.getColor()), PorterDuff.Mode.SRC_ATOP);
        bildKaffee.setColorFilter(res.getColor(zustandKaffee.getColor()), PorterDuff.Mode.SRC_ATOP);
        bildDruker.setColorFilter(res.getColor(zustandDruker.getColor()), PorterDuff.Mode.SRC_ATOP);
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