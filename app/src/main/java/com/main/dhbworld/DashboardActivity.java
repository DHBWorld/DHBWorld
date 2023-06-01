package com.main.dhbworld;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.Calendar.CalendarActivity;
import com.main.dhbworld.Calendar.nextEventsProvider;
import com.main.dhbworld.Debugging.Debugging;
import com.main.dhbworld.Dualis.EverlastingService;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.CurrentStatusListener;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.KVV.DataLoaderListener;
import com.main.dhbworld.KVV.Departure;
import com.main.dhbworld.KVV.Disruption;
import com.main.dhbworld.KVV.KVVDataLoader;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.Weather.WeatherApi;
import com.main.dhbworld.Weather.WeatherData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dhbw.timetable.rapla.data.event.Appointment;

public class DashboardActivity extends AppCompatActivity {


    private LinearLayout layoutCardMealPlan;
    private LinearLayout layoutCardCalendar;
    private LinearLayout layoutCardKvv;
    private LinearLayout layoutCardPI;
    private LinearLayout layoutCardInfo;

    SharedPreferences sp;

    public static final String MyPREFERENCES = "myPreferencesKey" ;
    public static final String dashboardSettings="dashboardSettings";
    FirebaseFirestore firestore;

    Boolean refreshIsEnable=true;
    Boolean configurationModus;
    Boolean cardCalendar_isVisible = true;
    Boolean cardPI_isVisible = true;
    Boolean cardMealPlan_isVisible = true;
    Boolean cardKvv_isVisible = true;
    Boolean cardInfo_isVisible = true;
    Boolean cardWeather_isVisible = true;

    MaterialCardView card_dash_calendar;
    MaterialCardView card_dash_pi;
    MaterialCardView card_dash_kvv;
    MaterialCardView card_dash_mealPlan;
    MaterialCardView card_dash_info;
    MaterialCardView card_dash_user_interaction;
    MaterialCardView card_dash_weather;

    private LinearLayout boxCardCalendar;
    private LinearLayout boxCardPI;
    private LinearLayout boxCardMealPlan;
    private LinearLayout boxCardKvv;
    private LinearLayout boxCardInfo;

    private LinearLayout card_dash_calendar_layout;
    private LinearLayout card_dash_pi_layout;
    private LinearLayout card_dash_kvv_layout;
    private LinearLayout card_dash_mealPlan_layout;
    private LinearLayout card_dash_user_interaction_layout;
    private LinearLayout card_dash_info_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Debugging.startDebugging(this);

        System.out.println("Running: " + EverlastingService.isRunning);

        if (!EverlastingService.isRunning) {
            startService(new Intent(this, EverlastingService.class));
        }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 23);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationUtilities.setUpNavigation(this,R.id.dashboard);
        defineViews();
        loadWeather();
        userConfigurationOfDashboard();
        loadUserInteraction();
        loadPersonalInformation();
        loadCalendar();
        if (NetworkAvailability.check(DashboardActivity.this)){
            loadMealPlan();
            loadKvv();
            if (cardInfo_isVisible){
                loadInfo();
            }
        } else {
            Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.problemsWithInternetConnection), BaseTransientBottomBar.LENGTH_LONG).show();
        }

    }

    private void defineViews(){
        layoutCardMealPlan= findViewById(R.id.layoutCardMealPlan);
        layoutCardCalendar = findViewById(R.id.layoutCardCalendar);
        layoutCardKvv = findViewById(R.id.layoutCardKvv);
        layoutCardPI = findViewById(R.id.layoutCardPI);
        layoutCardInfo = findViewById(R.id.layoutCardInfo);

        card_dash_calendar = findViewById(R.id.card_dash_calendar);
        card_dash_pi = findViewById(R.id.card_dash_pi);
        card_dash_kvv = findViewById(R.id.card_dash_kvv);
        card_dash_mealPlan = findViewById(R.id.card_dash_mealPlan);
        card_dash_info= findViewById(R.id.card_dash_info);
        card_dash_user_interaction= findViewById(R.id.card_dash_userInteraction);
        card_dash_weather = findViewById(R.id.card_dash_weather);

        boxCardCalendar= findViewById(R.id.buttonCardCalendar);
        boxCardPI= findViewById(R.id.buttonCardPI);
        boxCardMealPlan= findViewById(R.id.buttonCardMealPlan);
        boxCardKvv= findViewById(R.id.buttonCardKvv);
        boxCardInfo= findViewById(R.id.buttonCardInfo);

        card_dash_calendar_layout = findViewById(R.id.card_dash_calendar_layout);
        card_dash_pi_layout = findViewById(R.id.card_dash_pi_layout);
        card_dash_kvv_layout = findViewById(R.id.card_dash_kvv_layout);
        card_dash_mealPlan_layout = findViewById(R.id.card_dash_mealPlan_layout);
        card_dash_user_interaction_layout = findViewById(R.id.card_dash_userInteraction_layout);
        card_dash_info_layout = findViewById(R.id.card_dash_info_layout);
    }

    private void userConfigurationOfDashboard(){
        sp = getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);

        configurationModus=false;

        boxCardCalendar.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        boxCardPI.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        boxCardMealPlan.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        boxCardKvv.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        boxCardInfo.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));

        cardCalendar_isVisible = sp.getBoolean("cardCalendar", true);
        cardPI_isVisible = sp.getBoolean("cardPI", true);
        cardMealPlan_isVisible = sp.getBoolean("cardMealPlan", true);
        cardKvv_isVisible = sp.getBoolean("cardKvv", true);
        cardInfo_isVisible = sp.getBoolean("cardInfo", true);
        cardWeather_isVisible = sp.getBoolean("cardWeather", true);

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
        if (!cardInfo_isVisible){
            card_dash_info.setVisibility(View.GONE);
        }
        if (!cardWeather_isVisible) {
            card_dash_weather.setVisibility(View.GONE);
        }

        //TODO integrate onClickListeners more easily
        card_dash_calendar.setOnClickListener(new View.OnClickListener() {
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
        card_dash_pi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus) {
                    if (cardPI_isVisible) {
                        cardPI_isVisible = false; //True = Card is visible
                        card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(), 50));
                        card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(), 50));
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
        card_dash_kvv.setOnClickListener(new View.OnClickListener() {
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
        card_dash_mealPlan.setOnClickListener(new View.OnClickListener() {
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
        card_dash_user_interaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!configurationModus){
                    Intent intent = new Intent(DashboardActivity.this, UserInteraction.class);
                    startActivity(intent);
                }
            }
        });
        card_dash_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus) {
                    if (cardInfo_isVisible) {
                        cardInfo_isVisible = false; //True = Card is visible
                        card_dash_info.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(), 50));
                        card_dash_info_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(), 50));
                    } else {
                        cardInfo_isVisible = true;//True = Card is visible
                        card_dash_info.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(), 255));
                        card_dash_info_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(), 255));
                    }
                }else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/inFumumVerti/DHBWorld/releases/latest"));
                    startActivity(browserIntent);
                }
            }
        });
        card_dash_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus) {
                    if (cardWeather_isVisible) {
                        cardWeather_isVisible = false; //True = Card is visible
                        card_dash_weather.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_weather.getStrokeColor(), 50));
                    } else {
                        cardWeather_isVisible = true;//True = Card is visible
                        card_dash_weather.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_weather.getStrokeColor(), 255));
                    }
                }else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/inFumumVerti/DHBWorld/releases/latest"));
                    startActivity(browserIntent);
                }
            }
        });
    }

    private void loadCalendar(){
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(DashboardActivity.this);
        String url = preferences.getString("CurrentURL",null);

        ImageView uniImage = findViewById(R.id.imageViewCalendar);
        TextView nextClassView = findViewById(R.id.nextClassView);
        LinearLayout layoutTime = findViewById(R.id.layoutTimeCalendarCard);
        LinearLayout layoutCardCalendarInformation = findViewById(R.id.layoutCardCalendarInformation);
        LinearLayout layoutTimeDigit = findViewById(R.id.layoutTimeDigit);
        TextView timeView = findViewById(R.id.timeViewCalendarDashboard);
        TextView timeViewMin = findViewById(R.id.timeViewMinCalendarDashboard);
        TextView letterTimeView = findViewById(R.id.letterTimeViewCalendarDashboard);

        if (!(url ==null) && (!url.equals(""))) {
            layoutCardCalendarInformation.setVisibility(View.GONE);
            ProgressIndicator indicator= new ProgressIndicator(DashboardActivity.this, layoutCardCalendar);
            indicator.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nextEventsProvider nextEventsProvider = new nextEventsProvider(DashboardActivity.this);
                        Appointment nextClass = nextEventsProvider.getNextEvent();

                        layoutCardCalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                if(nextClass == null || nextClass.getStartDate() == null){
                                    indicator.hide();
                                    layoutCardCalendarInformation.setVisibility(View.VISIBLE);
                                    nextClassView.setText(getString(R.string.no_classes));
                                    layoutTimeDigit.setVisibility(View.GONE);
                                    layoutTime.setVisibility(View.GONE);
                                    timeView.setVisibility(View.GONE);
                                    timeViewMin.setVisibility(View.GONE);
                                    letterTimeView.setVisibility(View.GONE);
                                    uniImage.setBackground(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_pause));
                                }
                                else {
                                    LocalDateTime now = LocalDateTime.now();
                                    LocalDateTime startClass = nextClass.getStartDate();
                                    LocalDateTime endClass = nextClass.getEndDate();
                                    Duration durationUntilStartOfClass = Duration.between(now, startClass);
                                    Duration durationUntilEndOfClass = Duration.between(now, endClass);
                                    indicator.hide();
                                    layoutCardCalendarInformation.setVisibility(View.VISIBLE);
                                    if ((durationUntilStartOfClass.toHours() <= 8) && (durationUntilEndOfClass.toMinutes() >= 0)) {
                                        uniImage.setBackground(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_uni));
                                        nextClassView.setText(nextClass.getTitle());
                                        timeView.setText(nextClass.getStartTime());
                                        timeViewMin.setVisibility(View.VISIBLE);
                                        timeViewMin.setText(getResources().getString(R.string.min));
                                        if (durationUntilStartOfClass.toMinutes() >= 0) {
                                            new CountDownTimer(durationUntilStartOfClass.toMinutes() * 60000, 60000) {
                                                public void onTick(long millisUtilFinished) {
                                                    timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                                    letterTimeView.setText(getResources().getString(R.string.startsIn));
                                                    timeViewMin.setText(getResources().getString(R.string.min));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    timeView.setText(getResources().getString(R.string.now));
                                                    letterTimeView.setText("");
                                                    timeViewMin.setText("");
                                                }
                                            }.start();
                                        } else {
                                            new CountDownTimer(durationUntilEndOfClass.toMinutes() * 60000, 60000) {
                                                public void onTick(long millisUtilFinished) {
                                                    timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                                    letterTimeView.setText(getResources().getString(R.string.endsIn));
                                                    timeViewMin.setText(getResources().getString(R.string.min));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    timeView.setText("");
                                                    nextClassView.setText(getResources().getString(R.string.pause));
                                                    letterTimeView.setText("");
                                                    timeViewMin.setText("");
                                                    uniImage.setBackground(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_pause));
                                                }
                                            }.start();
                                        }
                                    } else {
                                        uniImage.setBackground(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_uni));
                                        nextClassView.setText(nextClass.getTitle());
                                        timeView.setText(nextClass.getStartTime());
                                        timeViewMin.setVisibility(View.GONE);
                                        letterTimeView.setText(startClass.getDayOfWeek().toString());
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        layoutCardCalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                indicator.hide();
                                layoutCardCalendarInformation.setVisibility(View.VISIBLE);
                                layoutTimeDigit.setVisibility(View.GONE);
                                layoutTime.setVisibility(View.GONE);
                                uniImage.setVisibility(View.GONE);
                                timeView.setVisibility(View.GONE);
                                timeViewMin.setVisibility(View.GONE);
                                letterTimeView.setVisibility(View.GONE);
                                nextClassView.setText(getResources().getString(R.string.problemsWithCalenderView));
                            }
                        });
                    }
                }
            }).start();
        }else{
            layoutTimeDigit.setVisibility(View.GONE);
            layoutTime.setVisibility(View.GONE);
            uniImage.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
            timeViewMin.setVisibility(View.GONE);
            letterTimeView.setVisibility(View.GONE);
            nextClassView.setText(getResources().getString(R.string.pasteLinkInCalender));

        }
    }

    private void loadKvv(){
        LinearLayout[] layoutTram = new LinearLayout[3];
        layoutTram[0] = findViewById(R.id.layoutDepartureOne);
        layoutTram[1] = findViewById(R.id.layoutDepartureTwo);
        layoutTram[2] = findViewById(R.id.layoutDepartureThree);
        ProgressIndicator indicator= new ProgressIndicator(DashboardActivity.this, layoutCardKvv, layoutTram);
        indicator.show();
        ImageView tramImageOne= findViewById(R.id.imageViewTramOne);
        TextView[] tramView = new TextView [3];
        tramView[0] = findViewById(R.id.textViewTramLineOne);
        tramView[1]= findViewById(R.id.textViewTramLineTwo);
        tramView[2] = findViewById(R.id.textViewTramLineThree);
        TextView[] platformView = new TextView [3];
        platformView[0] = findViewById(R.id.textViewTramPlatformOne);
        platformView[1] = findViewById(R.id.textViewTramPlatformTwo);
        platformView[2] = findViewById(R.id.textViewTramPlatformThree);
        TextView[] timeView = new TextView [3];
        timeView[0] = findViewById(R.id.textViewTramTimeOne);
        timeView[1] = findViewById(R.id.textViewTramTimeTwo);
        timeView[2]= findViewById(R.id.textViewTramTimeThree);

        KVVDataLoader dataLoader = new KVVDataLoader(this);
        dataLoader.setDataLoaderListener(new DataLoaderListener() {
            @Override
            public void onDataLoaded(ArrayList<Departure> departures, Disruption disruption) {
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
                indicator.hide();
                if ( (departures==null) || (departures.size()<1)){
                    tramImageOne.setBackground(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_pause));
                    platformView[0].setVisibility(View.GONE);
                    timeView[0].setVisibility(View.GONE);
                    tramView[0].setText(getResources().getString(R.string.serverTrouble));
                    layoutTram[2].setVisibility(View.GONE);
                    layoutTram[1].setVisibility(View.GONE);
                }else{
                    tramImageOne.setBackground(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_tram));
                    platformView[0].setVisibility(View.VISIBLE);
                    timeView[0].setVisibility(View.VISIBLE);
                    for (int i=0;i<3;i++){
                        if (departures.size()>i){
                            layoutTram[i].setVisibility(View.VISIBLE);
                            tramView[i].setText(departures.get(i).getLine().substring(departures.get(i).getLine().length()-1)+" ("+departures.get(i).getDestination()+")");
                            if (departures.get(i).isNotServiced()) {
                                platformView[i].setText(R.string.canceled_in_parens);
                            } else {
                                platformView[i].setText(departures.get(i).getPlatform());
                            }
                            timeView[i].setText(departures.get(i).getDepartureTime().format(formatter));
                        }else{
                            layoutTram[i].setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        LocalDateTime now = LocalDateTime.now();
        dataLoader.loadData(now);
    }

    private void loadWeather() {
        ImageView iconImageView = findViewById(R.id.weather_icon_imageview);
        TextView statusTextView = findViewById(R.id.weather_status_textview);

        WeatherApi weatherApi = new WeatherApi(WeatherApi.City.Karlsruhe);
        weatherApi.requestData(this, new WeatherApi.WeatherDataListener() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                iconImageView.setImageDrawable(weatherData.getIcon(DashboardActivity.this));
                statusTextView.setText(String.format("%s, %s°C", weatherData.getTranslatedWeatherCode(DashboardActivity.this), weatherData.getCurrentTemperature()));
            }

            @Override
            public void onError() {
                statusTextView.setText("Cannot get Weather");
                iconImageView.setImageDrawable(null);
            }
        });
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
                Snackbar.make(DashboardActivity.this.findViewById(android.R.id.content), getResources().getString(R.string.copied), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPersonalInformation(){
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
        markerTitle[0]=getString(R.string.matriculationNumber)+"\n";
        markerTitle[1]=getString(R.string.libraryNumber)+"\n" ;
        markerTitle[2]=getString(R.string.emailStudent)+"\n";
        Boolean emptyCard=true;
        String[] info = new String[3];
        info[0]=sp.getString("matriculationNumberKey", "");
        info[1]=sp.getString("libraryNumberKey", "");
        info[2]=sp.getString("studentMailKey", "");

        for (int i=0;i<3;i++){
            layoutInfo[i].setVisibility(View.VISIBLE);
            imageButtonCopy[i].setVisibility(View.VISIBLE);
            if ((!info[i].equals("")) && (!info[i].equals(" "))){
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
            infoView[0].setText(getResources().getString(R.string.thereIsntAnyPersonalData));
        }
    }

    private void loadMealPlan(){
        LinearLayout[] layoutMeal = new LinearLayout[3];
        layoutMeal[0] = findViewById(R.id.layoutMealOne);
        layoutMeal[1] = findViewById(R.id.layoutMealTwo);
        layoutMeal[2] = findViewById(R.id.layoutMealThree);
        TextView[] textViewMeal = new TextView[3];
        textViewMeal[0] = findViewById(R.id.textViewMealOne);
        textViewMeal[1] = findViewById(R.id.textViewMealTwo);
        textViewMeal[2] = findViewById(R.id.textViewMealThree);
        ProgressIndicator indicator= new ProgressIndicator(DashboardActivity.this, layoutCardMealPlan, layoutMeal);
        indicator.show();

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
               List<String> meals = CantineActivity.loadDataForDashboard();
                layoutCardMealPlan.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageViewMeal= findViewById(R.id.imageViewMeal);
                        indicator.hide();
                        if((meals==null) || (meals.size()==0)){
                            imageViewMeal.setImageResource(R.drawable.ic_no_meals);
                            textViewMeal[0].setText(R.string.thereIsntAnyInfo);
                            layoutMeal[1].setVisibility(View.GONE);
                            layoutMeal[2].setVisibility(View.GONE);
                        }else{
                            imageViewMeal.setImageResource(R.drawable.ic_restaurant);
                            for (int i=0;i<3;i++){
                                textViewMeal[i].setVisibility(View.VISIBLE);
                                if (meals.size()>i){
                                    textViewMeal[i].setText(meals.get(i));
                                }else{
                                    layoutMeal[i].setVisibility(View.GONE);
                                }
                            }
                        }

                    }
                });
            }
        }).start();
        }

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException{
            if (!configurationModus){
                if (refreshIsEnable){
                    refreshIsEnable=false;
                    new CountDownTimer(10000,1000) {
                        public void onTick(long millisUtilFinished) {
                        }
                        @Override
                        public void onFinish() {
                            refreshIsEnable=true;
                        }
                    }.start();
                    userConfigurationOfDashboard();
                    loadUserInteraction();
                    if (NetworkAvailability.check(DashboardActivity.this)){
                        loadMealPlan();
                        loadCalendar();
                        loadKvv();
                        if (cardInfo_isVisible){
                            loadInfo();
                        }
                    }else{
                        Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.problemsWithInternetConnection), BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                    loadPersonalInformation();

                }else {
                    Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.refreshIsOnlyIn10Min), BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.youAreInConfigModus), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
    }

    public void configurationClick(@NonNull MenuItem item) throws NullPointerException{
        sp = getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (!configurationModus){ //User can configure his dashboard
            item.setIcon(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_done));
            Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.chooseTheCardForConfiguration), BaseTransientBottomBar.LENGTH_SHORT).show();
            card_dash_user_interaction.setClickable(false);
            card_dash_calendar.setVisibility(View.VISIBLE);
            card_dash_pi.setVisibility(View.VISIBLE);
            card_dash_kvv.setVisibility(View.VISIBLE);
            card_dash_mealPlan.setVisibility(View.VISIBLE);
            card_dash_info.setVisibility(View.VISIBLE);
            card_dash_weather.setVisibility(View.VISIBLE);


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
            if (!cardInfo_isVisible){
                loadInfo();
                card_dash_info.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(),50));
                card_dash_info_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(),50));
            }
            if (!cardWeather_isVisible) {
                card_dash_weather.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_weather.getStrokeColor(),50));
                //card_dash_weather_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_weather.getStrokeColor(),50));
            }
            configurationModus=true;
        } else{
            item.setIcon(AppCompatResources.getDrawable(DashboardActivity.this, R.drawable.ic_construction));
            card_dash_user_interaction.setClickable(true);
            card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
            card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
            card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
            card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
            card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
            card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
            card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
            card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
            card_dash_info.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(),255));
            card_dash_info_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_info.getStrokeColor(),255));
            card_dash_weather.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_weather.getStrokeColor(),255));
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
            if (!cardInfo_isVisible){
                card_dash_info.setVisibility(View.GONE);
            }
            if (!cardWeather_isVisible) {
                card_dash_weather.setVisibility(View.GONE);
            }
            editor.putBoolean("cardCalendar", cardCalendar_isVisible);
            editor.putBoolean("cardPI", cardPI_isVisible);
            editor.putBoolean("cardMealPlan", cardMealPlan_isVisible);
            editor.putBoolean("cardKvv", cardKvv_isVisible);
            editor.putBoolean("cardInfo", cardInfo_isVisible);
            editor.putBoolean("cardWeather", cardWeather_isVisible);
            editor.apply();
            configurationModus=false;
        }
    }

    private void loadInfo() {
        TextView title = findViewById(R.id.textViewInfoTitle);
        TextView[] message = new TextView[3];
        message[0]=findViewById(R.id.textViewInfoMessageOne);
        message[1]=findViewById(R.id.textViewInfoMessageTwo);
        message[2]=findViewById(R.id.textViewInfoMessageThree);
        LinearLayout[] layoutInfoFull = new LinearLayout[1];
        layoutInfoFull[0] = findViewById(R.id.layoutInfo);
        LinearLayout[] layoutInfo = new LinearLayout[3];
        layoutInfo[0] = findViewById(R.id.layoutInfoMessageOne);
        layoutInfo[1] = findViewById(R.id.layoutInfoMessageTwo);
        layoutInfo[2] = findViewById(R.id.layoutInfoMessageThree);
        ProgressIndicator indicator= new ProgressIndicator(DashboardActivity.this,layoutCardInfo, layoutInfoFull);
        indicator.show();
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                layoutCardInfo.post(new Runnable() {
                    @Override
                    public void run() {
                        firestore= FirebaseFirestore.getInstance();
                        DocumentReference contact= firestore.collection("General").document("InfoCard");
                        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot doc= task.getResult();
                                    indicator.hide();
                                    title.setText(doc.getString("Title"));
                                    String m=doc.getString("Message");
                                    String[] parts = m.split("#");
                                    for (int i=0;i<3; i++){
                                        if (parts.length>i){
                                            message[i].setText(parts[i]);
                                        }else{
                                            layoutInfo[i].setVisibility(View.GONE);
                                        }
                                    }
                                    if (m.length()==0){
                                        layoutInfo[0].setVisibility(View.GONE);
                                    }

                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setCheckedItem(R.id.dashboard);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START, false);
        super.onResume();
    }
}