package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Debugging.Debugging;
import com.main.dhbworld.Enums.InteractionState;

import com.main.dhbworld.Firebase.CurrentStatusListener;
import com.main.dhbworld.Firebase.ReportCountListener;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.Services.UserInteractionMessagingService;
import com.main.dhbworld.UserInter.clickListeners.OnClickListenerConfirmationYES;

import com.main.dhbworld.UserInter.clickListeners.OnClickListenerConfirmationNO;
import com.main.dhbworld.UserInter.UserIntUtilities;

import java.util.ArrayList;
import java.util.Locale;

public class UserInteractionActivity extends AppCompatActivity {
    private ArrayList <Button> yesButtons;
    private ArrayList <Button> noButtons;

    private TextView stateCanteenTV;
    private TextView stateCoffeeTV;
    private TextView statePrinterTV;
    private TextView notificationCanteenTV;
    private TextView notificationCoffeeTV;
    private TextView notificationPrinterTV;

    private InteractionState stateCanteen;
    private InteractionState stateCoffee;
    private static InteractionState statePrinter;

    private long notificationCanteen;
    private long notificationCoffee;
    private long notificationPrinter;

    private LinearLayout imageBox_canteen;
    private LinearLayout imageBox_coffee;
    private LinearLayout imageBox_printer;

    private Utilities firebaseUtilities;
    private  ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Debugging.startDebugging(this);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int darkmode = Integer.parseInt(defaultSharedPreferences.getString("darkmode", "-1"));
        AppCompatDelegate.setDefaultNightMode(darkmode);

        String language = defaultSharedPreferences.getString("language", "default");
        if (language.equals("default")) {
            Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } else {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interaction_layout);

        NavigationUtilities.setUpNavigation(this, R.id.UserInteraction);

        UserInteractionMessagingService.createNotificationChannel(this, "warnings", "Warnungen (User Interaction)");

        stateManagement();
        yesNoButtonsManagement();
        notificationManagement();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Utilities.subscribeToTopics();

        setupFirebaseUtilities();
    }

    private void setupFirebaseUtilities(){
        firebaseUtilities = new Utilities(this);
        firebaseUtilities.setSignedInListener(new SignedInListenerUserInt());
        firebaseUtilities.setCurrentStatusListener(new CurrentStatusListenerUserInt());
        firebaseUtilities.setReportCountListener(new ReportCountListenerUserInt());
        firebaseUtilities.signIn();
    }
    private void yesNoButtonsSetup(){
        yesButtons =new ArrayList<> ();
        yesButtons.add(findViewById(R.id.yes0));
        yesButtons.add(findViewById(R.id.yes1));
        yesButtons.add(findViewById(R.id.yes2));


        noButtons =new ArrayList<> ();
        noButtons.add(findViewById(R.id.no0));
        noButtons.add(findViewById(R.id.no1));
        noButtons.add(findViewById(R.id.no2));
    }

    private void yesNoButtonsManagement(){
        yesNoButtonsSetup();

        InteractionState[][] states= new InteractionState[3][];
        states[0]= new InteractionState[]{InteractionState.QUEUE_SHORT, InteractionState.QUEUE_MIDDLE, InteractionState.QUEUE_LONG};
        states[1]= new InteractionState[]{InteractionState.CLEANING, InteractionState.DEFECT};

        configurateYesButtons(states);
        configurateNoButtons(states);



    }

    private void configurateYesButtons(InteractionState[][] states){
        for (Button button : yesButtons) {
            button.setOnClickListener(v -> {
                int buttonIndex = yesButtons.indexOf(button);
                DialogCofirmationUserInteraction confirmation;

                if (buttonIndex == 2) {
                    confirmation = new DialogCofirmationUserInteraction(UserInteractionActivity.this, R.string.moechten_sie_melden_dass_der_drucker_kaputt_ist, R.string.problem_report);
                } else {
                    confirmation = new DialogCofirmationUserInteraction(UserInteractionActivity.this, states[buttonIndex], R.string.problem_report);
                }
                confirmation.setPositiveButton(R.string.problem_report, new OnClickListenerConfirmationYES(firebaseUtilities, UserInteractionActivity.this.findViewById(android.R.id.content), buttonIndex, states, confirmation));
                confirmation.show();
            });

        }
    }
    private void  configurateNoButtons(InteractionState[][] states){
        for(Button button: noButtons){
            button.setOnClickListener(v -> {
                DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteractionActivity.this, R.string.problem_still_there, R.string.problem_is_solved);
                int buttonIndex = noButtons.indexOf(button);
                confirmation.setPositiveButton(R.string.problem_is_solved, new OnClickListenerConfirmationNO(firebaseUtilities, UserInteractionActivity.this.findViewById(android.R.id.content), buttonIndex, states, confirmation));
                confirmation.show();
            });
        }
    }
    private void setupViews(){
        stateCanteenTV = findViewById(R.id.state_canteen);
        stateCoffeeTV = findViewById(R.id.state_coffee);
        statePrinterTV = findViewById(R.id.state_printer);

        imageBox_canteen = findViewById(R.id.imageBox_canteen);
        imageBox_coffee = findViewById(R.id.imageBox_coffee);
        imageBox_printer = findViewById(R.id.imageBox_printer);
    }

    private void stateManagement(){
        setupViews();

        stateCanteen =InteractionState.QUEUE_ABCENT;
        stateCoffee =InteractionState.NORMAL;
        statePrinter =InteractionState.NORMAL;

        UserIntUtilities.setupCards(stateCanteenTV, stateCanteen, imageBox_canteen, UserInteractionActivity.this);
        UserIntUtilities.setupCards(stateCoffeeTV, stateCoffee, imageBox_coffee, UserInteractionActivity.this);
        UserIntUtilities.setupCards(statePrinterTV, statePrinter, imageBox_printer, UserInteractionActivity.this);
    }

    private void notificationManagement(){
        notificationCanteenTV = findViewById(R.id.previous_notifications_canteen);
        notificationCoffeeTV = findViewById(R.id.previous_notifications_coffee);
        notificationPrinterTV = findViewById(R.id.previous_notifications_printer);

        notificationCanteen= UserIntUtilities.setupNotifications(notificationCanteenTV, UserInteractionActivity.this);
        notificationCoffee=  UserIntUtilities.setupNotifications(notificationCoffeeTV, UserInteractionActivity.this);
        notificationPrinter=UserIntUtilities.setupNotifications(notificationPrinterTV, UserInteractionActivity.this);


    }

    private void updateInteractionState() {
        String[] categories = new String[]{Utilities.CATEGORY_CAFETERIA, Utilities.CATEGORY_COFFEE, Utilities.CATEGORY_PRINTER};
        for (int i=0; i<3; i++) {
            firebaseUtilities.getCurrentStatus(categories[i]);
            firebaseUtilities.getReportCount(categories[i]);
        }
    }

    class CurrentStatusListenerUserInt implements CurrentStatusListener{
        @Override
        public void onStatusReceived(String category, int status) {
            switch (category) {
                case Utilities.CATEGORY_CAFETERIA:

                    System.out.println("STATUS: " + status);
                    UserIntUtilities.changeStatus(stateCanteen,  stateCanteenTV,  imageBox_canteen,status, UserInteractionActivity.this );
                    break;
                case Utilities.CATEGORY_COFFEE:
                    UserIntUtilities.changeStatus(stateCoffee,  stateCoffeeTV,  imageBox_coffee,  status, UserInteractionActivity.this);
                    break;
                case Utilities.CATEGORY_PRINTER:
                    UserIntUtilities.changeStatus(statePrinter,  statePrinterTV,  imageBox_printer,  status, UserInteractionActivity.this);
                    break;
            }
        }
    }
    class ReportCountListenerUserInt implements ReportCountListener{
        @Override
        public void onReportCountReceived(String category, long reportCount) {
            switch (category) {
                case Utilities.CATEGORY_CAFETERIA:
                    UserIntUtilities.setPriviousNotofications(notificationCanteen, notificationCanteenTV, reportCount, UserInteractionActivity.this);
                    break;
                case Utilities.CATEGORY_COFFEE:
                    UserIntUtilities.setPriviousNotofications(notificationCoffee, notificationCoffeeTV, reportCount, UserInteractionActivity.this);
                    break;
                case Utilities.CATEGORY_PRINTER:
                    UserIntUtilities.setPriviousNotofications(notificationPrinter, notificationPrinterTV, reportCount, UserInteractionActivity.this);
                    break;
            }
        }
    }

    class SignedInListenerUserInt implements SignedInListener{

        @Override
        public void onSignedIn(FirebaseUser user) {
            progressDialog.dismiss();
            updateInteractionState();
        }

        @Override
        public void onSignInError() {
            progressDialog.dismiss();
            Snackbar.make(UserInteractionActivity.this.findViewById(android.R.id.content), getString(R.string.error), BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }



}


