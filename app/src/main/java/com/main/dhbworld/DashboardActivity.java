package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.time.*;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.CantineClasses.MealDailyPlan;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.CurrentStatusListener;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.KVV.DataLoaderListener;
import com.main.dhbworld.KVV.Departure;
import com.main.dhbworld.KVV.Disruption;
import com.main.dhbworld.KVV.KVVDataLoader;
import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.exceptions.NoConnectionException;
import android.os.CountDownTimer;

public class DashboardActivity extends AppCompatActivity {


    private LinearLayout layoutCardMealPlan;
    private LinearLayout layoutCardCalendar;
    private LinearLayout layoutCardKvv;
    private LinearLayout layoutCardPI;

    SharedPreferences sp;

    public static final String MyPREFERENCES = "myPreferencesKey" ;
    public static final String dashboardSettings="dashboardSettings";

    Boolean configurationModus;
    Boolean cardCalendar_isVisible = true;
    Boolean cardPI_isVisible = true;
    Boolean cardMealPlan_isVisible = true;
    Boolean cardKvv_isVisible = true;

    private MaterialCardView card_dash_calendar;
    private MaterialCardView card_dash_pi;
    private MaterialCardView card_dash_kvv;
    private MaterialCardView card_dash_mealPlan;

    private Button buttonCardCalendar;
    private Button buttonCardPI;
    private Button buttonCardMealPlan;
    private Button buttonCardKvv;

    private LinearLayout card_dash_calendar_layout;
    private LinearLayout card_dash_pi_layout;
    private LinearLayout card_dash_kvv_layout;
    private LinearLayout card_dash_mealPlan_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationUtilities.setUpNavigation(this,R.id.dashboard);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        layoutCardMealPlan= findViewById(R.id.layoutCardMealPlan);
        layoutCardCalendar = findViewById(R.id.layoutCardCalendar);
        layoutCardKvv = findViewById(R.id.layoutCardKvv);
        layoutCardPI = findViewById(R.id.layoutCardPI);

        card_dash_calendar = findViewById(R.id.card_dash_calendar);
        card_dash_pi = findViewById(R.id.card_dash_pi);
        card_dash_kvv = findViewById(R.id.card_dash_kvv);
        card_dash_mealPlan = findViewById(R.id.card_dash_mealPlan);

        userConfigurationOfDashboard();
        loadUserInteraction();
       if (NetworkAvailability.check(DashboardActivity.this)){
           loadMealPlan();
           loadCalendar();
           loadKvv();
       }else{
           card_dash_mealPlan.setVisibility(View.GONE);
           card_dash_calendar.setVisibility(View.GONE);
           card_dash_kvv.setVisibility(View.GONE);
           Toast.makeText(DashboardActivity.this, "Sie haben keine Internet-Verbindung, deshalb können die Daten nicht geladen werden.", Toast.LENGTH_LONG).show();
       }
       loadPersonalInformation();
    }

    private void userConfigurationOfDashboard(){
        sp = getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        configurationModus=false;
        buttonCardCalendar= findViewById(R.id.buttonCardCalendar);
        buttonCardPI= findViewById(R.id.buttonCardPI);
        buttonCardMealPlan= findViewById(R.id.buttonCardMealPlan);
        buttonCardKvv= findViewById(R.id.buttonCardKvv);

        buttonCardCalendar.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardPI.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardMealPlan.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardKvv.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));

        card_dash_calendar_layout = findViewById(R.id.card_dash_calendar_layout);
        card_dash_pi_layout = findViewById(R.id.card_dash_pi_layout);
        card_dash_kvv_layout = findViewById(R.id.card_dash_kvv_layout);
        card_dash_mealPlan_layout = findViewById(R.id.card_dash_mealPlan_layout);


        cardCalendar_isVisible = sp.getBoolean("cardCalendar", true);
        cardPI_isVisible = sp.getBoolean("cardPI", true);
        cardMealPlan_isVisible = sp.getBoolean("cardMealPlan", true);
        cardKvv_isVisible = sp.getBoolean("cardKvv", true);

       if (!cardCalendar_isVisible){
           card_dash_calendar.setVisibility(View.GONE);
       }
        if (!cardPI_isVisible){
            card_dash_pi.setVisibility(View.GONE);
        }
        if (!cardMealPlan_isVisible){
            card_dash_mealPlan.setVisibility(View.GONE);
        }
        if (!cardKvv_isVisible){
            card_dash_kvv.setVisibility(View.GONE);
        }

        buttonCardCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus){
                    if (cardCalendar_isVisible){
                        cardCalendar_isVisible=false;
                        card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                        card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                    }else{
                        cardCalendar_isVisible=true;
                        card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                        card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                    }
                }else{
                    Intent intent = new Intent(DashboardActivity.this, CalendarActivity.class);
                    startActivity(intent);

                }
            }
        });

        buttonCardPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus) {
                    if (cardPI_isVisible) {
                        cardPI_isVisible = false; //True = Card is visible
                        card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(), 50));
                        card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(), 50));
                    } else {
                        cardPI_isVisible = true;//True = Card is visible
                        card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(), 255));
                        card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(), 255));
                    }
                }else{
                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });

        buttonCardMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus) {
                    if (cardMealPlan_isVisible) {
                        cardMealPlan_isVisible = false; //True = Card is visible
                        card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 50));
                        card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 50));
                    } else {
                        cardMealPlan_isVisible = true;//True = Card is visible
                        card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 255));
                        card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 255));
                    }
                }else{
                    Intent intent = new Intent(DashboardActivity.this, CantineActivity.class);
                    startActivity(intent);

                }
            }
        });
        buttonCardKvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus) {
                    if (cardKvv_isVisible) {
                        cardKvv_isVisible = false; //True = Card is visible
                        card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(), 50));
                        card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(), 50));
                    } else {
                        cardKvv_isVisible = true;//True = Card is visible
                        card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(), 255));
                        card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(), 255));
                    }
                }else{
                    Intent intent = new Intent(DashboardActivity.this, KVVActivity.class);
                    startActivity(intent);

                }
            }
        });

    }

    private void loadCalendar(){
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(DashboardActivity.this);
        String url = preferences.getString("CurrentURL",null);

        ImageView UniImage = findViewById(R.id.imageViewCalendar);
        TextView nextClassView = findViewById(R.id.nextClassView);
        LinearLayout layoutTime = findViewById(R.id.layoutTimeCalendarCard);
        LinearLayout layoutTimeDigit = findViewById(R.id.layoutTimeDigit);
        TextView timeView = findViewById(R.id.timeViewCalendarDashboard);
        TextView timeViewMin = findViewById(R.id.timeViewMinCalendarDashboard);
        TextView letterTimeView = findViewById(R.id.letterTimeViewCalendarDashboard);

        if (!(url ==null) && (!url.equals(""))) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        nextEventsProvider nextEventsProvider = new nextEventsProvider(DashboardActivity.this);
                        Appointment nextClass = nextEventsProvider.getNextEvent();
                        layoutCardCalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime startClass = nextClass.getStartDate();
                                LocalDateTime endClass = nextClass.getEndDate();
                                Duration durationUntilStartOfClass = Duration.between(now, startClass);
                                Duration durationUntilEndOfClass = Duration.between(now, endClass);
                                if ((durationUntilStartOfClass.toHours() <= 8) && (durationUntilEndOfClass.toMinutes() >= 0)) {
                                    UniImage.setImageResource(R.drawable.ic_uni);
                                    nextClassView.setText(nextClass.getTitle());
                                    timeView.setText(nextClass.getStartTime());
                                    timeViewMin.setVisibility(View.VISIBLE);
                                    timeViewMin.setText(" Min");
                                    if (durationUntilStartOfClass.toMinutes() >= 0) {
                                        new CountDownTimer(durationUntilStartOfClass.toMinutes() * 60000, 60000) {
                                            public void onTick(long millisUtilFinished) {
                                                timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                                letterTimeView.setText("start in ");
                                                timeViewMin.setText(" Min");
                                            }
                                            @Override
                                            public void onFinish() {
                                                timeView.setText("now");
                                                letterTimeView.setText("");
                                                timeViewMin.setText("");
                                            }
                                        }.start();
                                    } else {
                                        new CountDownTimer(durationUntilEndOfClass.toMinutes() * 60000, 60000) {
                                            public void onTick(long millisUtilFinished) {
                                                timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                                letterTimeView.setText("end in ");
                                                timeViewMin.setText(" Min");
                                            }
                                            @Override
                                            public void onFinish() {
                                                timeView.setText("");
                                                nextClassView.setText("Pause!");
                                                letterTimeView.setText("");
                                                timeViewMin.setText("");
                                                UniImage.setImageResource(R.drawable.ic_pause);
                                            }
                                        }.start();
                                    }
                                } else {
                                    UniImage.setImageResource(R.drawable.ic_uni);
                                    nextClassView.setText(nextClass.getTitle());
                                    timeView.setText(nextClass.getStartTime());
                                    timeViewMin.setVisibility(View.GONE);
                                    letterTimeView.setText(startClass.getDayOfWeek().toString());
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        layoutCardCalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                layoutTimeDigit.setVisibility(View.GONE);
                                layoutTime.setVisibility(View.GONE);
                                UniImage.setVisibility(View.GONE);
                                timeView.setVisibility(View.GONE);
                                timeViewMin.setVisibility(View.GONE);
                                letterTimeView.setVisibility(View.GONE);
                                nextClassView.setText("Die Daten aus dem Kalender können hier nicht gezeigt werden.");
                            }
                        });
                    }
                }
            }).start();
        }else{
            layoutTimeDigit.setVisibility(View.GONE);
            layoutTime.setVisibility(View.GONE);
            UniImage.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
            timeViewMin.setVisibility(View.GONE);
            letterTimeView.setVisibility(View.GONE);
            nextClassView.setText("Damit Sie die Daten aus dem Rapla hier sehen können, fügen Sie bitte den Link in dem Kalender hinzu.");

        }
    }

    private void loadKvv(){
        KVVDataLoader dataLoader = new KVVDataLoader(this);
        dataLoader.setDataLoaderListener(new DataLoaderListener() {
            @Override
            public void onDataLoaded(ArrayList<Departure> departures, Disruption disruption) {
                ImageView tramImageOne= findViewById(R.id.imageViewTramOne);
                TextView tramViewOne = findViewById(R.id.textViewTramLineOne);
                TextView platformViewOne = findViewById(R.id.textViewTramPlatformOne);
                TextView timeViewOne = findViewById(R.id.textViewTramTimeOne);
                LinearLayout layoutTramTwo = findViewById(R.id.layoutDepartureTwo);
                TextView tramViewTwo = findViewById(R.id.textViewTramLineTwo);
                TextView platformViewTwo = findViewById(R.id.textViewTramPlatformTwo);
                TextView timeViewTwo = findViewById(R.id.textViewTramTimeTwo);
                LinearLayout layoutTramThree = findViewById(R.id.layoutDepartureThree);
                TextView tramViewThree = findViewById(R.id.textViewTramLineThree);
                TextView platformViewThree = findViewById(R.id.textViewTramPlatformThree);
                TextView timeViewThree = findViewById(R.id.textViewTramTimeThree);
                layoutTramTwo.setVisibility(View.GONE);
                layoutTramThree.setVisibility(View.GONE);
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
                if (departures.size()<1){
                    tramImageOne.setBackground(getDrawable(R.drawable.ic_pause));
                    platformViewOne.setVisibility(View.GONE);
                    timeViewOne.setVisibility(View.GONE);
                    tramViewOne.setText("Server Problem");
                } if (departures.size()>0){
                    tramViewOne.setText(departures.get(0).getLine().substring(departures.get(0).getLine().length()-1)+" ("+departures.get(0).getDestination()+")");
                    platformViewOne.setText(departures.get(0).getPlatform());
                    timeViewOne.setText(departures.get(0).getDepartureTime().format(formatter));
                 } if (departures.size()>1){
                    layoutTramTwo.setVisibility(View.VISIBLE);
                    tramViewTwo.setText(departures.get(1).getLine().substring(departures.get(1).getLine().length()-1)+" ("+departures.get(1).getDestination()+")");
                    platformViewTwo.setText(departures.get(1).getPlatform());
                    timeViewTwo.setText(departures.get(1).getDepartureTime().format(formatter));
                }  if (departures.size()>2){
                    layoutTramThree.setVisibility(View.VISIBLE);
                    tramViewThree.setText(departures.get(2).getLine().substring(departures.get(2).getLine().length()-1)+" ("+departures.get(2).getDestination()+")");
                    platformViewThree.setText(departures.get(2).getPlatform());
                    timeViewThree.setText(departures.get(2).getDepartureTime().format(formatter));
                }
            }
        });
        LocalDateTime now = LocalDateTime.now();
        dataLoader.loadData(now);
    }

    private void loadUserInteraction(){
        Utilities utilities = new Utilities(this);
        ImageView image_canteen = findViewById(R.id.imageBox_dashboard_canteen);
        ImageView image_coffee= findViewById(R.id.imageBox_dashboard_coffee);
        ImageView image_printer = findViewById(R.id.imageBox_dashboard_printer);
        image_canteen.setColorFilter(ContextCompat.getColor(this, R.color.grey_light));
        image_coffee.setColorFilter(ContextCompat.getColor(this, R.color.grey_light));
        image_printer.setColorFilter(ContextCompat.getColor(this, R.color.grey_light));

        utilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                utilities.setCurrentStatusListener(new CurrentStatusListener() {
                    @Override
                    public void onStatusReceived(String category, int status) {
                        switch (category) {
                            case Utilities.CATEGORY_CAFETERIA:
                                InteractionState stateCanteen = InteractionState.parseId(status);
                                image_canteen.setColorFilter(getColor(stateCanteen.getColor()));
                                break;
                            case Utilities.CATEGORY_COFFEE:
                                InteractionState stateCoffee = InteractionState.parseId(status);
                                image_coffee.setColorFilter(getColor(stateCoffee.getColor()));
                                break;
                            case Utilities.CATEGORY_PRINTER:
                                InteractionState statePrinter = InteractionState.parseId(status);
                                image_printer.setColorFilter(getColor(statePrinter.getColor()));
                                break;
                        }
                    }
                });
                utilities.getCurrentStatus(Utilities.CATEGORY_CAFETERIA);
                utilities.getCurrentStatus(Utilities.CATEGORY_PRINTER);
                utilities.getCurrentStatus(Utilities.CATEGORY_COFFEE);
            }

            @Override
            public void onSignInError() {

            }
        });
        utilities.signIn();
    }

    private void copyClick(ImageButton button, String copyText){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", copyText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(DashboardActivity.this, "Kopiert", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPersonalInformation(){
        String MyPREFERENCES = "myPreferencesKey" ;
        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        LinearLayout[] layoutInfo = new LinearLayout[3];
        layoutInfo[0] = findViewById(R.id.layoutInfoOne);
        layoutInfo[1] = findViewById(R.id.layoutInfoTwo);
        layoutInfo[2] = findViewById(R.id.layoutInfoThree);
        ImageButton[] imageButtonCopy = new ImageButton[3];
        imageButtonCopy[0] = findViewById(R.id.imageButtonCopyOne);
        imageButtonCopy[1] = findViewById(R.id.imageButtonCopyTwo);
        imageButtonCopy[2] = findViewById(R.id.imageButtonCopyThree);
        TextView[] infoView = new TextView[3];
        infoView[0] = findViewById(R.id.textViewPersonalInfoOne);
        infoView[1] = findViewById(R.id.textViewPersonalInfoTwo);
        infoView[2] = findViewById(R.id.textViewPersonalInfoThree);
        String[] markerTitle = new String[3];
        markerTitle[0]="Matrikelnummer:\n";
        markerTitle[1]="Bibliotheksnummer:\n" ;
        markerTitle[2]="E-Mail:\n" ;
        Boolean emptyCard=true;
        String[] info = new String[3];
        info[0]=sp.getString("matriculationNumberKey", "");
        info[1]=sp.getString("libraryNumberKey", "");
        info[2]=sp.getString("studentMailKey", "");

        for (int i=0;i<3;i++){
            layoutInfo[i].setVisibility(View.VISIBLE);
            imageButtonCopy[i].setVisibility(View.VISIBLE);
            if (!info[i].equals("")){
                emptyCard=false;
                infoView[i].setText( markerTitle[i]+info[i]);
                copyClick(imageButtonCopy[i], info[i]);
            }else{
                layoutInfo[i].setVisibility(View.GONE);
            }
        }
        if (emptyCard){
            layoutInfo[0].setVisibility(View.VISIBLE);
            imageButtonCopy[0].setVisibility(View.GONE);
            infoView[0].setText("Sie haben noch keine persönlichen Daten gespeichert");
        }
    }

    private void loadMealPlan(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
               List<String> meals = CantineActivity.loadDataForDashboard();
                layoutCardMealPlan.post(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout[] layoutMeal = new LinearLayout[3];
                        layoutMeal[0] = findViewById(R.id.layoutMealOne);
                        layoutMeal[1] = findViewById(R.id.layoutMealTwo);
                        layoutMeal[2] = findViewById(R.id.layoutMealThree);
                        TextView[] textViewMeal = new TextView[3];
                        textViewMeal[0] = findViewById(R.id.textViewMealOne);
                        textViewMeal[1] = findViewById(R.id.textViewMealTwo);
                        textViewMeal[2] = findViewById(R.id.textViewMealThree);
                        ImageView imageViewMeal= findViewById(R.id.imageViewMeal);
                        imageViewMeal.setImageResource(R.drawable.ic_restaurant);

                        if((meals==null) || (meals.size()==0)){
                            imageViewMeal.setImageResource(R.drawable.ic_no_meals);
                            textViewMeal[0].setText("Es gibt keine Daten für heute");
                            layoutMeal[1].setVisibility(View.GONE);
                            layoutMeal[2].setVisibility(View.GONE);
                        }else{
                            for (int i=0;i<3;i++){
                                textViewMeal[i].setVisibility(View.VISIBLE);

                                if (meals.size()>i){
                                    textViewMeal[i].setText(meals.get(i));
                                }else{
                                    layoutMeal[0].setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
            }
        }).start();
        }

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException{
        if (configurationModus==false){
            userConfigurationOfDashboard();
            loadUserInteraction();
            if (NetworkAvailability.check(DashboardActivity.this)){
                card_dash_mealPlan.setVisibility(View.VISIBLE);
                card_dash_calendar.setVisibility(View.VISIBLE);
                card_dash_kvv.setVisibility(View.VISIBLE);
               loadMealPlan();
               loadCalendar();
               loadKvv();
            }else{
                card_dash_mealPlan.setVisibility(View.GONE);
                card_dash_calendar.setVisibility(View.GONE);
                card_dash_kvv.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, "Sie haben keine Internet-Verbindung, deshalb können die Daten nicht geladen werden.", Toast.LENGTH_LONG).show();
            }
            loadPersonalInformation();
        }else {
            Toast.makeText(DashboardActivity.this, "Sie sind in Konfiguration-Modus, keine Aktualisierung ist möglich", Toast.LENGTH_LONG).show();
        }
    }

    public void configurationClick(@NonNull MenuItem item) throws NullPointerException{
        sp = getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (configurationModus==false){ //User can configure his dashboard
            item.setIcon(getResources().getDrawable(R.drawable.ic_done));
            Toast.makeText(DashboardActivity.this, " Wählen sie Cards, die ausblenden oder wieder einblenden möchten", Toast.LENGTH_LONG).show();
            card_dash_calendar.setVisibility(View.VISIBLE);
            card_dash_pi.setVisibility(View.VISIBLE);
            card_dash_kvv.setVisibility(View.VISIBLE);
            card_dash_mealPlan.setVisibility(View.VISIBLE);

            buttonCardCalendar.setVisibility(View.VISIBLE);
            buttonCardPI.setVisibility(View.VISIBLE);
            buttonCardMealPlan.setVisibility(View.VISIBLE);
            buttonCardKvv.setVisibility(View.VISIBLE);

            if (!cardCalendar_isVisible){
                card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
            }
            if (!cardPI_isVisible){
                card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
                card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
            }
            if (!cardMealPlan_isVisible){
                card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
                card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
            }
            if (!cardKvv_isVisible){
                card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
                card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
            }
            configurationModus=true;
        } else{
           item.setIcon(getResources().getDrawable(R.drawable.ic_construction));
            card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
            card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
            card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
            card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
            card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
            card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
            card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
            card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));

            if (!cardCalendar_isVisible){
                card_dash_calendar.setVisibility(View.GONE);
            }
            if (!cardPI_isVisible){
                card_dash_pi.setVisibility(View.GONE);
            }
            if (!cardMealPlan_isVisible){
                card_dash_mealPlan.setVisibility(View.GONE);
            }
            if (!cardKvv_isVisible){
                card_dash_kvv.setVisibility(View.GONE);
            }
            editor.putBoolean("cardCalendar", cardCalendar_isVisible);
            editor.putBoolean("cardPI", cardPI_isVisible);
            editor.putBoolean("cardMealPlan", cardMealPlan_isVisible);
            editor.putBoolean("cardKvv", cardKvv_isVisible);
            configurationModus=false;

        }
    }
}