package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Enums.InteractionState;

import com.main.dhbworld.Firebase.CurrentStatusListener;
import com.main.dhbworld.Firebase.DataSendListener;
import com.main.dhbworld.Firebase.ReportCountListener;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
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

    private long meldungenKantine;
    private long meldungenKaffee;
    private long meldungenDruker;

    private LinearLayout imageBox_kantine;
    private LinearLayout imageBox_kaffee;
    private LinearLayout imageBox_drucker;

    private Utilities firebaseUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interaction_layout);

        zustandManagement();
        jaNeinButtonsManagement();
        meldungenManagement();

        firebaseUtilities = new Utilities(this);
        firebaseUtilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                Toast.makeText(UserInteraction.this, "Angemeldet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSignInError() {

            }
        });
        firebaseUtilities.setCurrentStatusListener(new CurrentStatusListener() {
            @Override
            public void onStatusReceived(String category, int status) {
                //TODO bisherige Meldungen vom Server holen
                Resources res = getResources();
                switch (category) {
                    case Utilities.CATEGORY_CAFETERIA:
                        zustandKantine = InteractionState.parseId(status);
                        zustandKantineTV.setText(getResources().getString(R.string.zustand) + " " + zustandKantine.getText());
                        imageBox_kantine.setBackgroundColor(res.getColor(zustandKantine.getColor()));
                        break;
                    case Utilities.CATEGORY_COFFEE:
                        zustandKaffee = InteractionState.parseId(status);
                        zustandKaffeeTV.setText(getResources().getString(R.string.zustand) + " " + zustandKaffee.getText());
                        imageBox_kaffee.setBackgroundColor(res.getColor(zustandKaffee.getColor()));
                        break;
                    case Utilities.CATEGORY_PRINTER:
                        zustandDruker = InteractionState.parseId(status);
                        zustandDrukerTV.setText(getResources().getString(R.string.zustand) + " " + zustandDruker.getText());
                        imageBox_drucker.setBackgroundColor(res.getColor(zustandDruker.getColor()));
                        break;
                }
            }
        });

        firebaseUtilities.setReportCountListener(new ReportCountListener() {
            @Override
            public void onReportCountReceived(String category, long reportCount) {
                switch (category) {
                    case Utilities.CATEGORY_CAFETERIA:
                        meldungenKantine = reportCount;
                        meldungenKantineTV.setText(getResources().getString(R.string.bisherigen_meldungen) + " " + reportCount);
                        break;
                    case Utilities.CATEGORY_COFFEE:
                        meldungenKaffee = reportCount;
                        meldungenKaffeeTV.setText(getResources().getString(R.string.bisherigen_meldungen) + " " + reportCount);
                        break;
                    case Utilities.CATEGORY_PRINTER:
                        meldungenDruker = reportCount;
                        meldungenDrukerTV.setText(getResources().getString(R.string.bisherigen_meldungen) + " " + reportCount);
                        break;
                }
            }
        });

        firebaseUtilities.signIn();

        updateInteractionState();

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

        InteractionState[][] states= new InteractionState[3][];
        states[0]= new InteractionState[]{InteractionState.QUEUE_KURZ, InteractionState.QUEUE_MIDDLE, InteractionState.QUEUE_LONG};
        states[1]= new InteractionState[]{InteractionState.CLEANING, InteractionState.DEFECT};
        //states[2]= new String[]{"", ""};

        for(Button j:ja){
            if (j.equals(ja.get(2))) {
                continue;
            }
            j.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, states[ja.indexOf(j)] , R.string.problem_melden);
                    confirmation.setPositiveButton(R.string.problem_melden, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firebaseUtilities.setDataSendListener(new DataSendListener() {
                                @Override
                                public void success() {
                                    //TODO Toast anpassen
                                    Toast.makeText(UserInteraction.this, "Gesendet", Toast.LENGTH_SHORT).show();
                                    updateInteractionState();
                                }

                                @Override
                                public void failed(Exception reason) {
                                    Toast.makeText(UserInteraction.this, "Fehler", Toast.LENGTH_SHORT).show();
                                }
                            });

                            String category = "";
                            if (j.equals(ja.get(0))) {
                                category = Utilities.CATEGORY_CAFETERIA;
                            } else if (j.equals(ja.get(1))) {
                                category = Utilities.CATEGORY_COFFEE;
                            }
                            firebaseUtilities.addToDatabase(category, confirmation.getSelectedState().getId());
                        }
                    });
                    confirmation.show();
                }});

        }

        ja.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_melden_dass_der_drucker_kaputt_ist, R.string.problem_melden);
                confirmation.show();

                confirmation.setPositiveButton(R.string.problem_melden, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseUtilities.setDataSendListener(new DataSendListener() {
                            @Override
                            public void success() {
                                //TODO Toast anpassen
                                Toast.makeText(UserInteraction.this, "Gesendet", Toast.LENGTH_SHORT).show();
                                updateInteractionState();
                            }

                            @Override
                            public void failed(Exception reason) {
                                Toast.makeText(UserInteraction.this, "Fehler", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String category = Utilities.CATEGORY_PRINTER;
                        firebaseUtilities.addToDatabase(category, InteractionState.DEFECT.getId());
                    }
                });
            }
        });


        for(Button n:nein){
            n.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_das_problem_besteht_nicht_mehr, R.string.problem_ist_geloest);
                    confirmation.setPositiveButton(R.string.problem_ist_geloest, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firebaseUtilities.setDataSendListener(new DataSendListener() {
                                @Override
                                public void success() {
                                    //TODO Toast anpassen
                                    Toast.makeText(UserInteraction.this, "Gesendet", Toast.LENGTH_SHORT).show();
                                    updateInteractionState();
                                }

                                @Override
                                public void failed(Exception reason) {
                                    Toast.makeText(UserInteraction.this, "Fehler", Toast.LENGTH_SHORT).show();
                                }
                            });

                            String category = "";
                            int id = 0;
                            if (n.equals(nein.get(0))) {
                                category = Utilities.CATEGORY_CAFETERIA;
                                id = 3;
                            } else if (n.equals(nein.get(1))) {
                                category = Utilities.CATEGORY_COFFEE;
                            } else if (n.equals(nein.get(2))) {
                                category = Utilities.CATEGORY_PRINTER;
                            }
                            firebaseUtilities.addToDatabase(category, id);
                        }
                    });
                    confirmation.show();
                }
            });
        }

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
        meldungenKaffee=0;
        meldungenDruker=0;

        meldungenKantineTV.setText(getResources().getString(R.string.bisherigen_meldungen)+" "+meldungenKantine);
        meldungenKaffeeTV.setText(getResources().getString(R.string.bisherigen_meldungen)+" "+meldungenKaffee);
        meldungenDrukerTV.setText(getResources().getString(R.string.bisherigen_meldungen)+" "+meldungenDruker);

    }

    private void updateInteractionState() {
        String[] categories = new String[]{Utilities.CATEGORY_CAFETERIA, Utilities.CATEGORY_COFFEE, Utilities.CATEGORY_PRINTER};
        for (int i=0; i<3; i++) {
            firebaseUtilities.getCurrentStatus(categories[i]);
            firebaseUtilities.getReportCount(categories[i]);
        }
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

    public long getMeldungenKantine() {
        return meldungenKantine;
    }

    public void setMeldungenKantine(int meldungenKantine) {
        this.meldungenKantine = meldungenKantine;
    }

    public long getMeldungenKaffee() {
        return meldungenKaffee;
    }

    public void setMeldungenKaffee(int meldungenKaffee) {
        this.meldungenKaffee = meldungenKaffee;
    }

    public long getMeldungenDruker() {
        return meldungenDruker;
    }

    public void setMeldungenDruker(int meldungenDruker) {
        this.meldungenDruker = meldungenDruker;
    }
}