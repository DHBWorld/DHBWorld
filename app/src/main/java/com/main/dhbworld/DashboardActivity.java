package com.main.dhbworld;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.main.dhbworld.Dashboard.Cards.CardType;
import com.main.dhbworld.Dashboard.Cards.DashboardCard;
import com.main.dhbworld.Dashboard.Cards.DashboardCardInfo;
import com.main.dhbworld.Dashboard.Cards.DashboardCardUserInt;
import com.main.dhbworld.Dashboard.Cards.DashboardCardWeather;
import com.main.dhbworld.Dashboard.Dashboard;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoadListenerKVV;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderCalendar;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderInfo;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderMealPlan;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderPersonalInfo;
import com.main.dhbworld.Dashboard.DataLoaders.DataLoaderUserInt;
import com.main.dhbworld.Debugging.Debugging;
import com.main.dhbworld.Dualis.EverlastingService;
import com.main.dhbworld.KVV.KVVDataLoader;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.Utilities.ProgressIndicator;
import com.main.dhbworld.Weather.Forecast;
import com.main.dhbworld.Weather.WeatherApi;
import com.main.dhbworld.Weather.WeatherData;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {


    private LinearLayout layoutCardMealPlan;
    private LinearLayout layoutCardCalendar;
    private LinearLayout layoutCardKvv;
    private LinearLayout layoutCardPI;
    private LinearLayout layoutCardInfo;


    public static final String MyPREFERENCES = "myPreferencesKey";

    Boolean refreshIsEnable = true;
    Boolean cardInfo_isVisible = true;


    MaterialCardView card_dash_calendar;
    MaterialCardView card_dash_pi;
    MaterialCardView card_dash_kvv;
    MaterialCardView card_dash_mealPlan;
    MaterialCardView card_dash_info;
    MaterialCardView card_dash_user_interaction;
    MaterialCardView card_dash_weather;

    DashboardCard dashboardCardMealPlan;
    DashboardCard dashboardCarCalender;
    DashboardCard dashboardKvv;
    DashboardCard dashboardPI;
    DashboardCard dashboardCardInfo;
    DashboardCard dashboardCardWeather;
    DashboardCard dashboardCardUserInt;

    private Dashboard dashboard;

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
        NavigationUtilities.setUpNavigation(this, R.id.dashboard);
        defineViews();
        loadWeather();
        loadUserInteraction();
        loadPersonalInformation();
        loadCalendar();
        if (NetworkAvailability.check(DashboardActivity.this)) {
            loadMealPlan();
            loadKvv();
            if (cardInfo_isVisible) {
                loadInfo();
            }
        } else {
            Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.problemsWithInternetConnection), BaseTransientBottomBar.LENGTH_LONG).show();
        }

    }

    private void defineViews() {
        layoutCardMealPlan = findViewById(R.id.layoutCardMealPlan);
        layoutCardCalendar = findViewById(R.id.layoutCardCalendar);
        layoutCardKvv = findViewById(R.id.layoutCardKvv);
        layoutCardPI = findViewById(R.id.layoutCardPI);
        layoutCardInfo = findViewById(R.id.layoutCardInfo);

        card_dash_calendar = findViewById(R.id.card_dash_calendar);
        card_dash_pi = findViewById(R.id.card_dash_pi);
        card_dash_kvv = findViewById(R.id.card_dash_kvv);
        card_dash_mealPlan = findViewById(R.id.card_dash_mealPlan);
        card_dash_info = findViewById(R.id.card_dash_info);
        card_dash_user_interaction = findViewById(R.id.card_dash_userInteraction);
        card_dash_weather = findViewById(R.id.card_dash_weather);

        LinearLayout boxCardCalendar = findViewById(R.id.buttonCardCalendar);
        LinearLayout boxCardPI = findViewById(R.id.buttonCardPI);
        LinearLayout boxCardMealPlan = findViewById(R.id.buttonCardMealPlan);
        LinearLayout boxCardKvv = findViewById(R.id.buttonCardKvv);
        LinearLayout boxCardInfo = findViewById(R.id.buttonCardInfo);

        LinearLayout card_dash_calendar_layout = findViewById(R.id.card_dash_calendar_layout);
        LinearLayout card_dash_pi_layout = findViewById(R.id.card_dash_pi_layout);
        LinearLayout card_dash_kvv_layout = findViewById(R.id.card_dash_kvv_layout);
        LinearLayout card_dash_mealPlan_layout = findViewById(R.id.card_dash_mealPlan_layout);
        LinearLayout card_dash_user_interaction_layout = findViewById(R.id.card_dash_userInteraction_layout);
        LinearLayout card_dash_info_layout = findViewById(R.id.card_dash_info_layout);

        LinearLayout forecastLayout = findViewById(R.id.weather_forecast);

        dashboardCardMealPlan = new DashboardCard(CardType.CARD_MEAL_PLAN, layoutCardMealPlan, card_dash_mealPlan, boxCardMealPlan, card_dash_mealPlan_layout);
        dashboardCarCalender = new DashboardCard(CardType.CARD_CALENDAR, layoutCardCalendar, card_dash_calendar, boxCardCalendar, card_dash_calendar_layout);
        dashboardKvv = new DashboardCard(CardType.CARD_KVV, layoutCardKvv, card_dash_kvv, boxCardKvv, card_dash_kvv_layout);
        dashboardPI = new DashboardCard(CardType.CARD_PI, layoutCardPI, card_dash_pi, boxCardPI, card_dash_pi_layout);
        dashboardCardInfo = new DashboardCardInfo(CardType.CARD_INFO, layoutCardInfo, card_dash_info, boxCardInfo, card_dash_info_layout);
        dashboardCardWeather = new DashboardCardWeather(CardType.CARD_WEATHER, null, card_dash_weather, null, null, forecastLayout);
        dashboardCardUserInt = new DashboardCardUserInt(CardType.CARD_USER_INTERACTION, null, card_dash_user_interaction, null, card_dash_user_interaction_layout);

        dashboard = new Dashboard();
        dashboard.addCard(dashboardCardMealPlan);
        dashboard.addCard(dashboardCarCalender);
        dashboard.addCard(dashboardKvv);
        dashboard.addCard(dashboardPI);
        dashboard.addCard(dashboardCardInfo);
        dashboard.addCard(dashboardCardWeather);
        dashboard.addCard(dashboardCardUserInt);

        dashboard.setUp(this.getApplicationContext(), getResources().getColor(R.color.black), DashboardActivity.this);


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
        ProgressIndicator indicator = new ProgressIndicator(DashboardActivity.this, layoutCardKvv, layoutTram);
        indicator.show();
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

        KVVDataLoader dataLoader = new KVVDataLoader(this);
        dataLoader.setDataLoaderListener(new DataLoadListenerKVV(layoutTram, indicator, tramView, platformView, timeView, tramImageOne, DashboardActivity.this));
        LocalDateTime now = LocalDateTime.now();
        dataLoader.loadData(now);
    }

    private void loadWeather() {
        ImageView iconImageView = findViewById(R.id.weather_icon_imageview);
        TextView statusTextView = findViewById(R.id.weather_status_textview);


        TextView weatherLocation = findViewById(R.id.weather_location);

        TextView day1 = findViewById(R.id.forecast_day_1);
        TextView day2 = findViewById(R.id.forecast_day_2);
        TextView day3 = findViewById(R.id.forecast_day_3);
        TextView day4 = findViewById(R.id.forecast_day_4);

        TextView maxTempDay1 = findViewById(R.id.forecast_temp_max_1);
        TextView maxTempDay2 = findViewById(R.id.forecast_temp_max_2);
        TextView maxTempDay3 = findViewById(R.id.forecast_temp_max_3);
        TextView maxTempDay4 = findViewById(R.id.forecast_temp_max_4);

        TextView minTempDay1 = findViewById(R.id.forecast_temp_min_1);
        TextView minTempDay2 = findViewById(R.id.forecast_temp_min_2);
        TextView minTempDay3 = findViewById(R.id.forecast_temp_min_3);
        TextView minTempDay4 = findViewById(R.id.forecast_temp_min_4);

        ImageView iconDay1 = findViewById(R.id.weather_icon_imageview_1);
        ImageView iconDay2 = findViewById(R.id.weather_icon_imageview_2);
        ImageView iconDay3 = findViewById(R.id.weather_icon_imageview_3);
        ImageView iconDay4 = findViewById(R.id.weather_icon_imageview_4);

        WeatherApi weatherApi = new WeatherApi(WeatherApi.City.Karlsruhe);

        weatherLocation.setText(WeatherApi.City.Karlsruhe.toString());

        weatherApi.requestData(this, new WeatherApi.WeatherDataListener() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                iconImageView.setImageDrawable(weatherData.getIcon(DashboardActivity.this));
                statusTextView.setText(String.format("%s, %s°C", weatherData.getTranslatedWeatherCode(DashboardActivity.this), weatherData.getCurrentTemperature()));

                ArrayList<Forecast> forecasts = weatherData.getForecasts();
                if (forecasts.size() < 5) {
                    return;
                }
                day1.setText(forecasts.get(1).getTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                day2.setText(forecasts.get(2).getTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                day3.setText(forecasts.get(3).getTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                day4.setText(forecasts.get(4).getTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));

                maxTempDay1.setText(String.format("%s°C", forecasts.get(1).getMaxTemperatureRounded()));
                maxTempDay2.setText(String.format("%s°C", forecasts.get(2).getMaxTemperatureRounded()));
                maxTempDay3.setText(String.format("%s°C", forecasts.get(3).getMaxTemperatureRounded()));
                maxTempDay4.setText(String.format("%s°C", forecasts.get(4).getMaxTemperatureRounded()));

                minTempDay1.setText(String.format("%s°C", forecasts.get(1).getMinTemperatureRounded()));
                minTempDay2.setText(String.format("%s°C", forecasts.get(2).getMinTemperatureRounded()));
                minTempDay3.setText(String.format("%s°C", forecasts.get(3).getMinTemperatureRounded()));
                minTempDay4.setText(String.format("%s°C", forecasts.get(4).getMinTemperatureRounded()));

                iconDay1.setImageDrawable(forecasts.get(1).getIcon(DashboardActivity.this));
                iconDay2.setImageDrawable(forecasts.get(2).getIcon(DashboardActivity.this));
                iconDay3.setImageDrawable(forecasts.get(3).getIcon(DashboardActivity.this));
                iconDay4.setImageDrawable(forecasts.get(4).getIcon(DashboardActivity.this));
            }

            @Override
            public void onError() {
                statusTextView.setText(R.string.cannot_get_weather);
                iconImageView.setImageDrawable(null);
            }
        });
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

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException {
        if (!dashboard.getConfigurationModus()) {
            if (refreshIsEnable) {
                refreshIsEnable = false;
                new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUtilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        refreshIsEnable = true;
                    }
                }.start();
                loadUserInteraction();
                if (NetworkAvailability.check(DashboardActivity.this)) {
                    loadMealPlan();
                    loadCalendar();
                    loadKvv();
                    loadWeather();
                    if (cardInfo_isVisible) {
                        loadInfo();
                    }
                } else {
                    Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.problemsWithInternetConnection), BaseTransientBottomBar.LENGTH_LONG).show();
                }
                loadPersonalInformation();

            } else {
                Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.refreshIsOnlyIn10Min), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.youAreInConfigModus), BaseTransientBottomBar.LENGTH_SHORT).show();
        }
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
        dataLoaderInfo.load();

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