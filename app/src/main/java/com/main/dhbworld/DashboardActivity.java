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
       if (isNetworkAvailable(DashboardActivity.this)){
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



        //ImageButton settings = findViewById(R.id.dashboard_settings);
        configurationModus=false;
        buttonCardCalendar= findViewById(R.id.buttonCardCalendar);
        buttonCardPI= findViewById(R.id.buttonCardPI);
        buttonCardMealPlan= findViewById(R.id.buttonCardMealPlan);
        buttonCardKvv= findViewById(R.id.buttonCardKvv);

        buttonCardCalendar.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardPI.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardMealPlan.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardKvv.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));

        buttonCardCalendar.setVisibility(View.INVISIBLE);
        buttonCardPI.setVisibility(View.INVISIBLE);
        buttonCardMealPlan.setVisibility(View.INVISIBLE);
        buttonCardKvv.setVisibility(View.INVISIBLE);

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
                if (cardCalendar_isVisible){
                    cardCalendar_isVisible=false;
                    card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                    card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                }else{
                    cardCalendar_isVisible=true;
                    card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                    card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                }
            }
        });

        buttonCardPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardPI_isVisible){
                    cardPI_isVisible=false; //True = Card is visible
                    card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
                    card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
                }else{
                    cardPI_isVisible=true;//True = Card is visible
                    card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
                    card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
                }

            }
        });

        buttonCardMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardMealPlan_isVisible){
                    cardMealPlan_isVisible=false; //True = Card is visible
                    card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
                    card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
                }else{
                    cardMealPlan_isVisible=true;//True = Card is visible
                    card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
                    card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
                }

            }
        });
        buttonCardKvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardKvv_isVisible){
                    cardKvv_isVisible=false; //True = Card is visible
                    card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
                    card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
                }else{
                    cardKvv_isVisible=true;//True = Card is visible
                    card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
                    card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
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
        timeViewMin.setVisibility(View.VISIBLE);
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
                                    if (durationUntilStartOfClass.toMinutes() >= 0) {
                                        new CountDownTimer(durationUntilStartOfClass.toMinutes() * 60000, 60000) {
                                            public void onTick(long millisUtilFinished) {
                                                timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                                letterTimeView.setText("start in ");
                                                timeViewMin.setText("Min");
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
                                                timeViewMin.setText("Min");
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
                                } else if (durationUntilStartOfClass.toHours() > 9) {
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.d("NetworkCheck", "isNetworkAvailable: No");
            return false;
        }

        // get network info for all of the data interfaces (e.g. WiFi, 3G, LTE, etc.)
        NetworkInfo[] info = connectivity.getAllNetworkInfo();

        // make sure that there is at least one interface to test against
        if (info != null) {
            // iterate through the interfaces
            for (int i = 0; i < info.length; i++) {
                // check this interface for a connected state
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    Log.d("NetworkCheck", "isNetworkAvailable: Yes");
                    return true;
                }
            }
        }
        return false;}

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

       // InteractionState statePrinter= UserInteraction.getStatePrinter();


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


      /*if (statePrinter!=null){
        image_printer.setColorFilter(statePrinter.getColor());
      }*/

    }

    private void loadPersonalInformation(){
        String MyPREFERENCES = "myPreferencesKey" ;
        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        LinearLayout layoutInfoOne = findViewById(R.id.layoutInfoOne);
        LinearLayout layoutInfoTwo = findViewById(R.id.layoutInfoTwo);
        LinearLayout layoutInfoThree = findViewById(R.id.layoutInfoThree);
        ImageButton imageButtonCopyOne = findViewById(R.id.imageButtonCopyOne);
        ImageButton imageButtonCopyTwo = findViewById(R.id.imageButtonCopyTwo);
        ImageButton imageButtonCopyThree = findViewById(R.id.imageButtonCopyThree);
        TextView infoOne = findViewById(R.id.textViewPersonalInfoOne);
        TextView infoTwo = findViewById(R.id.textViewPersonalInfoTwo);
        TextView infoThree = findViewById(R.id.textViewPersonalInfoThree);
        imageButtonCopyOne.setVisibility(View.VISIBLE);
        imageButtonCopyTwo.setVisibility(View.VISIBLE);
        imageButtonCopyThree.setVisibility(View.VISIBLE);
        layoutInfoOne.setVisibility(View.VISIBLE);
        layoutInfoTwo.setVisibility(View.VISIBLE);
        layoutInfoThree.setVisibility(View.VISIBLE);
        Boolean emptyCard=true;
        String infoM= sp.getString("matriculationNumberKey", "");
        if (!infoM.equals("")){
            emptyCard=false;
            infoOne.setText("Bibliotheksnummer:\n"+infoM);
            imageButtonCopyOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", infoM);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DashboardActivity.this, "Kopiert", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            layoutInfoOne.setVisibility(View.GONE);
        }
        String infoL= sp.getString("libraryNumberKey", "");
        if (!infoL.equals("")){
            emptyCard=false;
            infoTwo.setText("Bibliotheksnummer:\n"+infoL);
            imageButtonCopyTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", infoL);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DashboardActivity.this, "Kopiert", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            layoutInfoTwo.setVisibility(View.GONE);
        }
        String infoE= sp.getString("studentMailKey", "");
        if (!infoE.equals("")){
            emptyCard=false;
            infoThree.setText("E-Mail:\n"+infoE);
            imageButtonCopyThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", infoE);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DashboardActivity.this, "Kopiert", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            layoutInfoThree.setVisibility(View.GONE);
        }
        if (emptyCard){
            imageButtonCopyOne.setVisibility(View.GONE);
            infoOne.setText("Sie haben noch keine persönlichen Daten gespeichert");
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
                        LinearLayout layoutMealOne= findViewById(R.id.layoutMealOne);
                        LinearLayout layoutMealTwo= findViewById(R.id.layoutMealTwo);
                        LinearLayout layoutMealThree= findViewById(R.id.layoutMealThree);
                        TextView textViewMealOne= findViewById(R.id.textViewMealOne);
                        TextView textViewMealTwo= findViewById(R.id.textViewMealTwo);
                        TextView textViewMealThree= findViewById(R.id.textViewMealThree);
                        ImageView imageViewMeal= findViewById(R.id.imageViewMeal);
                        imageViewMeal.setImageResource(R.drawable.ic_restaurant);
                        layoutMealOne.setVisibility(View.VISIBLE);
                        layoutMealTwo.setVisibility(View.VISIBLE);
                        layoutMealThree.setVisibility(View.VISIBLE);
                        if (meals.size()>0){
                            textViewMealOne.setText(meals.get(0));
                        }else{
                            layoutMealOne.setVisibility(View.GONE);
                        }
                        if (meals.size()>1){
                            textViewMealTwo.setText(meals.get(1));
                        }else{
                            layoutMealTwo.setVisibility(View.GONE);
                        }
                        if (meals.size()>2){
                            textViewMealThree.setText(meals.get(2));
                        }else{
                            layoutMealThree.setVisibility(View.GONE);
                        }
                       if (meals.size()==0){
                           imageViewMeal.setImageResource(R.drawable.ic_no_meals);
                           textViewMealOne.setText("Es gibt keine Daten für heute");
                        }
                    }
                });
            }
        }).start();
        }

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException{
        userConfigurationOfDashboard();
        loadUserInteraction();
        if (isNetworkAvailable(DashboardActivity.this)){
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

    public void configurationClick(@NonNull MenuItem item) throws NullPointerException{
        sp = getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (configurationModus==false){ //User can configure his dashboard

            item.setIcon(getResources().getDrawable(R.drawable.ic_done));


          // item.setBackground(getResources().getDrawable(R.drawable.ic_done));
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
            buttonCardCalendar.setVisibility(View.INVISIBLE);
            buttonCardPI.setVisibility(View.INVISIBLE);
            buttonCardMealPlan.setVisibility(View.INVISIBLE);
            buttonCardKvv.setVisibility(View.INVISIBLE);


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