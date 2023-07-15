package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.R;
import com.main.dhbworld.Weather.Forecast;
import com.main.dhbworld.Weather.WeatherApi;
import com.main.dhbworld.Weather.WeatherData;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DataLoaderWeather {

    private final CircularProgressIndicator progressIndicator;
    private final LinearLayout mainWeatherLayout;
    private final ImageView iconImageView ;
    private final TextView statusTextView ;
    private final TextView weatherLocation ;
    private final TextView[] day ;
    private final TextView[] maxTempDay ;
    private final TextView[] minTempDay;
    private final ImageView[] iconDay ;
    private final Context context;


    public DataLoaderWeather(Context context, CircularProgressIndicator progressIndicator, LinearLayout mainWeatherLayout, ImageView iconImageView, TextView statusTextView, TextView weatherLocation, TextView[] day, TextView[] maxTempDay, TextView[] minTempDay, ImageView[] iconDay) {
    this.context=context;
    this.progressIndicator = progressIndicator;
    this.mainWeatherLayout = mainWeatherLayout;
    this.iconImageView=iconImageView;
    this.statusTextView=statusTextView;
    this.weatherLocation=weatherLocation;
    this.day=day;
    this.maxTempDay=maxTempDay;
    this.minTempDay=minTempDay;
    this.iconDay=iconDay;

    }

    public void load(){
        WeatherApi weatherApi = new WeatherApi(WeatherApi.City.Karlsruhe);

        weatherLocation.setText(WeatherApi.City.Karlsruhe.toString());

        weatherApi.requestData(context, new WeatherApi.WeatherDataListener() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                progressIndicator.setVisibility(View.GONE);
                mainWeatherLayout.setVisibility(View.VISIBLE);
                iconImageView.setImageDrawable(weatherData.getIcon(context));
                statusTextView.setText(String.format("%s, %s°C", weatherData.getTranslatedWeatherCode(context), weatherData.getCurrentTemperature()));

                ArrayList<Forecast> forecasts = weatherData.getForecasts();
                if (forecasts.size() < 5) {
                    return;
                }

                for (int i=0; i<4; i++){
                    day[i].setText(forecasts.get(i).getTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                    maxTempDay[i].setText(String.format("%s°C", forecasts.get(i).getMaxTemperatureRounded()));
                    minTempDay[i].setText(String.format("%s°C", forecasts.get(i).getMinTemperatureRounded()));
                    iconDay[i].setImageDrawable(forecasts.get(i).getIcon(context));
                }
            }

            @Override
            public void onError() {
                progressIndicator.setVisibility(View.GONE);
                mainWeatherLayout.setVisibility(View.VISIBLE);
                statusTextView.setText(R.string.cannot_get_weather);
                iconImageView.setImageDrawable(null);
            }
        });
    }
}
