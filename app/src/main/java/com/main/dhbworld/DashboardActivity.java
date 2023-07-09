package com.main.dhbworld;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderBb;
import com.main.dhbworld.Utilities.RefreshCounter;
import com.main.dhbworld.Dashboard.Cards.CardType;
import com.main.dhbworld.Dashboard.Cards.DashboardCard;
import com.main.dhbworld.Dashboard.Cards.DashboardCardInfo;
import com.main.dhbworld.Dashboard.Cards.DashboardCardUserInt;
import com.main.dhbworld.Dashboard.Cards.DashboardCardWeather;
import com.main.dhbworld.Dashboard.Dashboard;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderCalendar;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderInfo;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderKVV;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderMealPlan;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderPersonalInfo;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderUserInt;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderWeather;
import com.main.dhbworld.Debugging.Debugging;
import com.main.dhbworld.Dualis.service.EverlastingService;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.Utilities.NetworkAvailability;
import com.main.dhbworld.Dashboard.DashboardRefresh;


import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {


    private LinearLayout layoutCardMealPlan;
    private LinearLayout layoutCardCalendar;
    private LinearLayout layoutCardKvv;
    private LinearLayout layoutCardPI;
    private LinearLayout layoutCardInfo;
    private LinearLayout layoutCardBb;


    public static final String MyPREFERENCES = "myPreferencesKey";


    private MaterialCardView card_dash_calendar;
    private MaterialCardView card_dash_pi;
    private MaterialCardView card_dash_kvv;
    private MaterialCardView card_dash_mealPlan;
    private MaterialCardView card_dash_info;
    private MaterialCardView card_dash_user_interaction;
    private MaterialCardView card_dash_weather;
    private MaterialCardView card_dash_bb;

    private DashboardCard dashboardCardInfo;

    private Dashboard dashboard;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Debugging.startDebugging(this);

        System.out.println("Running: " + EverlastingService.isRunning);

        if (!EverlastingService.isRunning) {
            startForegroundService(new Intent(this, EverlastingService.class));
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
        NavigationUtilities.setUpNavigation(this, R.id.dashboard);
        firestore=FirebaseFirestore.getInstance();
        defineViews();
        buildDashboard();

        loadDashboard();

    }

    private void loadDashboardCards() {
        loadUserInteraction();
        loadBlackBoard();
        loadPersonalInformation();
        if (!NetworkAvailability.check(DashboardActivity.this)) {
            Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.problemsWithInternetConnection), BaseTransientBottomBar.LENGTH_LONG).show();
            return;
        }
        loadMealPlan();
        loadCalendar();
        loadKvv();
        loadWeather();
        if (dashboardCardInfo.cardIsVisible()) {
            loadInfo();
        }


    }

    private void loadBlackBoard() {
        LinearLayout layoutAdverTags = findViewById(R.id.layoutAdverTags);
        LinearLayout layoutAdvertisement = findViewById(R.id.layoutAdvertisement);
        TextView textViewAdvertTitle = findViewById(R.id.textViewAdvertTitle);

        DataLoaderBb dataLoaderBb = new DataLoaderBb(DashboardActivity.this,   layoutAdvertisement,  textViewAdvertTitle,  layoutAdverTags,  layoutCardBb);
        dataLoaderBb.load(firestore);
    }

    private void defineViews() {
        layoutCardMealPlan = findViewById(R.id.layoutCardMealPlan);
        layoutCardCalendar = findViewById(R.id.layoutCardCalendar);
        layoutCardKvv = findViewById(R.id.layoutCardKvv);
        layoutCardPI = findViewById(R.id.layoutCardPI);
        layoutCardInfo = findViewById(R.id.layoutCardInfo);
        layoutCardBb = findViewById(R.id.layoutCardBb);

        card_dash_calendar = findViewById(R.id.card_dash_calendar);
        card_dash_pi = findViewById(R.id.card_dash_pi);
        card_dash_kvv = findViewById(R.id.card_dash_kvv);
        card_dash_mealPlan = findViewById(R.id.card_dash_mealPlan);
        card_dash_info = findViewById(R.id.card_dash_info);
        card_dash_user_interaction = findViewById(R.id.card_dash_userInteraction);
        card_dash_weather = findViewById(R.id.card_dash_weather);
        card_dash_bb = findViewById(R.id.card_dash_bb);

    }

    private void buildDashboard(){
        LinearLayout boxCardCalendar = findViewById(R.id.buttonCardCalendar);
        LinearLayout boxCardPI = findViewById(R.id.buttonCardPI);
        LinearLayout boxCardMealPlan = findViewById(R.id.buttonCardMealPlan);
        LinearLayout boxCardKvv = findViewById(R.id.buttonCardKvv);
        LinearLayout boxCardInfo = findViewById(R.id.buttonCardInfo);
        LinearLayout boxCardBb = findViewById(R.id.buttonCardBb);

        LinearLayout card_dash_calendar_layout = findViewById(R.id.card_dash_calendar_layout);
        LinearLayout card_dash_pi_layout = findViewById(R.id.card_dash_pi_layout);
        LinearLayout card_dash_kvv_layout = findViewById(R.id.card_dash_kvv_layout);
        LinearLayout card_dash_mealPlan_layout = findViewById(R.id.card_dash_mealPlan_layout);
        LinearLayout card_dash_user_interaction_layout = findViewById(R.id.card_dash_userInteraction_layout);
        LinearLayout card_dash_info_layout = findViewById(R.id.card_dash_info_layout);
        LinearLayout card_dash_bb_layout = findViewById(R.id.card_dash_bb_layout);

        LinearLayout forecastLayout = findViewById(R.id.weather_forecast);


        dashboardCardInfo = new DashboardCardInfo(CardType.CARD_INFO, layoutCardInfo, card_dash_info, boxCardInfo, card_dash_info_layout);
        dashboard = new Dashboard();
        dashboard.addCard(new DashboardCard(CardType.CARD_MEAL_PLAN, layoutCardMealPlan, card_dash_mealPlan, boxCardMealPlan, card_dash_mealPlan_layout));
        dashboard.addCard(new DashboardCard(CardType.CARD_CALENDAR, layoutCardCalendar, card_dash_calendar, boxCardCalendar, card_dash_calendar_layout));
        dashboard.addCard(new DashboardCard(CardType.CARD_KVV, layoutCardKvv, card_dash_kvv, boxCardKvv, card_dash_kvv_layout));
        dashboard.addCard( new DashboardCard(CardType.CARD_PI, layoutCardPI, card_dash_pi, boxCardPI, card_dash_pi_layout));
        dashboard.addCard( new DashboardCard(CardType.CARD_BB, layoutCardBb, card_dash_bb, boxCardBb, card_dash_bb_layout));
        dashboard.addCard(dashboardCardInfo);
        dashboard.addCard(new DashboardCardWeather(CardType.CARD_WEATHER, null, card_dash_weather, null, null, forecastLayout));
        dashboard.addCard(new DashboardCardUserInt(CardType.CARD_USER_INTERACTION, null, card_dash_user_interaction, null, card_dash_user_interaction_layout));
        dashboard.setUp(this.getApplicationContext(),  DashboardActivity.this);

    }

    private void loadCalendar() {
        ImageView uniImage = findViewById(R.id.imageViewCalendar);
        TextView nextClassView = findViewById(R.id.nextClassView);
        LinearLayout layoutTime = findViewById(R.id.layoutTimeCalendarCard);
        LinearLayout layoutCardCalendarInformation = findViewById(R.id.layoutCardCalendarInformation);
        LinearLayout layoutTimeDigit = findViewById(R.id.layoutTimeDigit);
        TextView timeView = findViewById(R.id.timeViewCalendarDashboard);
        TextView timeViewMin = findViewById(R.id.timeViewMinCalendarDashboard);
        TextView letterTimeView = findViewById(R.id.letterTimeViewCalendarDashboard);

        DataLoaderCalendar dataLoaderCalendar = new DataLoaderCalendar(DashboardActivity.this, layoutCardCalendar, uniImage, nextClassView, layoutTime, layoutCardCalendarInformation, layoutTimeDigit, timeView, timeViewMin, letterTimeView);
        dataLoaderCalendar.load();
    }

    private void loadKvv() {
        LinearLayout[] layoutTram = new LinearLayout[3];
        layoutTram[0] = findViewById(R.id.layoutDepartureOne);
        layoutTram[1] = findViewById(R.id.layoutDepartureTwo);
        layoutTram[2] = findViewById(R.id.layoutDepartureThree);
        ImageView tramImageOne = findViewById(R.id.imageViewTramOne);
        TextView[] tramView = new TextView[3];
        tramView[0] = findViewById(R.id.textViewTramLineOne);
        tramView[1] = findViewById(R.id.textViewTramLineTwo);
        tramView[2] = findViewById(R.id.textViewTramLineThree);
        TextView[] platformView = new TextView[3];
        platformView[0] = findViewById(R.id.textViewTramPlatformOne);
        platformView[1] = findViewById(R.id.textViewTramPlatformTwo);
        platformView[2] = findViewById(R.id.textViewTramPlatformThree);
        TextView[] timeView = new TextView[3];
        timeView[0] = findViewById(R.id.textViewTramTimeOne);
        timeView[1] = findViewById(R.id.textViewTramTimeTwo);
        timeView[2] = findViewById(R.id.textViewTramTimeThree);

        DataLoaderKVV dataLoaderKVV = new DataLoaderKVV(layoutTram, tramView, platformView, timeView, tramImageOne, this, DashboardActivity.this, layoutCardKvv);
        dataLoaderKVV.load();
    }

    private void loadWeather() {
        ImageView iconImageView = findViewById(R.id.weather_icon_imageview);
        TextView statusTextView = findViewById(R.id.weather_status_textview);
        TextView weatherLocation = findViewById(R.id.weather_location);

        TextView[] day = new TextView[4];
        day[0] = findViewById(R.id.forecast_day_1);
        day[1] = findViewById(R.id.forecast_day_2);
        day[2] = findViewById(R.id.forecast_day_3);
        day[3] = findViewById(R.id.forecast_day_4);
        TextView[] maxTempDay = new TextView[4];
        maxTempDay[0] = findViewById(R.id.forecast_temp_max_1);
        maxTempDay[1] = findViewById(R.id.forecast_temp_max_2);
        maxTempDay[2] = findViewById(R.id.forecast_temp_max_3);
        maxTempDay[3] = findViewById(R.id.forecast_temp_max_4);
        TextView[] minTempDay = new TextView[4];
        minTempDay[0] = findViewById(R.id.forecast_temp_min_1);
        minTempDay[1] = findViewById(R.id.forecast_temp_min_2);
        minTempDay[2] = findViewById(R.id.forecast_temp_min_3);
        minTempDay[3] = findViewById(R.id.forecast_temp_min_4);
        ImageView[] iconDay = new ImageView[4];
        iconDay[0] = findViewById(R.id.weather_icon_imageview_1);
        iconDay[1] = findViewById(R.id.weather_icon_imageview_2);
        iconDay[2] = findViewById(R.id.weather_icon_imageview_3);
        iconDay[3] = findViewById(R.id.weather_icon_imageview_4);

        DataLoaderWeather dataLoaderWeather = new DataLoaderWeather(this,iconImageView, statusTextView , weatherLocation, day, maxTempDay, minTempDay, iconDay  );
        dataLoaderWeather.load();

    }

    private void loadUserInteraction() {
        ImageView image_canteen = findViewById(R.id.imageBox_dashboard_canteen);
        ImageView image_coffee = findViewById(R.id.imageBox_dashboard_coffee);
        ImageView image_printer = findViewById(R.id.imageBox_dashboard_printer);
        DataLoaderUserInt dataLoaderUserInt = new DataLoaderUserInt(this, image_canteen, image_coffee, image_printer);
        dataLoaderUserInt.load();
    }

    private void loadPersonalInformation() {
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

        DataLoaderPersonalInfo dataLoaderPersonalInfo = new DataLoaderPersonalInfo(this, layoutInfo, imageButtonCopy, infoView, DashboardActivity.this.findViewById(android.R.id.content));
        dataLoaderPersonalInfo.load();
    }

    private void loadMealPlan() {
        LinearLayout[] layoutMeal = new LinearLayout[3];
        layoutMeal[0] = findViewById(R.id.layoutMealOne);
        layoutMeal[1] = findViewById(R.id.layoutMealTwo);
        layoutMeal[2] = findViewById(R.id.layoutMealThree);
        TextView[] textViewMeal = new TextView[3];
        textViewMeal[0] = findViewById(R.id.textViewMealOne);
        textViewMeal[1] = findViewById(R.id.textViewMealTwo);
        textViewMeal[2] = findViewById(R.id.textViewMealThree);

        ImageView imageViewMeal = findViewById(R.id.imageViewMeal);

        DataLoaderMealPlan dataLoaderMealPlan = new DataLoaderMealPlan(DashboardActivity.this, layoutCardMealPlan, layoutMeal, textViewMeal, imageViewMeal);
        dataLoaderMealPlan.load();
    }
    private void loadDashboard(){
        boolean doRefresh = DashboardRefresh.statusCheck(dashboard.getConfigurationModus(), dashboard.getRefreshStatus(), this.findViewById(android.R.id.content), this);

        if (doRefresh) {
            new RefreshCounter(dashboard).start();
            loadDashboardCards();
        }
    }

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException {
       loadDashboard();
    }


    public void configurationClick(@NonNull MenuItem item) throws NullPointerException {

        View snackbarContent = this.findViewById(android.R.id.content);
        if (!dashboardCardInfo.cardIsVisible()) {
            loadInfo();
        }
        String message = getResources().getString(R.string.chooseTheCardForConfiguration);
        dashboard.configurationClick(item, this.getApplicationContext(), snackbarContent, message);
    }

    private void loadInfo() {
        TextView title = findViewById(R.id.textViewInfoTitle);
        TextView[] message = new TextView[3];
        message[0] = findViewById(R.id.textViewInfoMessageOne);
        message[1] = findViewById(R.id.textViewInfoMessageTwo);
        message[2] = findViewById(R.id.textViewInfoMessageThree);
        LinearLayout[] layoutInfoFull = new LinearLayout[1];
        layoutInfoFull[0] = findViewById(R.id.layoutInfo);
        LinearLayout[] layoutInfo = new LinearLayout[3];
        layoutInfo[0] = findViewById(R.id.layoutInfoMessageOne);
        layoutInfo[1] = findViewById(R.id.layoutInfoMessageTwo);
        layoutInfo[2] = findViewById(R.id.layoutInfoMessageThree);

        DataLoaderInfo dataLoaderInfo = new DataLoaderInfo(this, title, message, layoutInfoFull, layoutInfo, layoutCardInfo);
        dataLoaderInfo.load(firestore);

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