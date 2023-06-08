package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.R;
import com.main.dhbworld.Weather.Forecast;
import com.main.dhbworld.Weather.WeatherApi;
import com.main.dhbworld.Weather.WeatherData;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DataLoaderWeather {
    ImageView iconImageView ;
    TextView statusTextView ;

    TextView weatherLocation ;

    TextView[] day ;

    TextView[] maxTempDay ;

    TextView[] minTempDay;

    ImageView[] iconDay ;
    Context context;


    public DataLoaderWeather(Context context, ImageView iconImageView, TextView statusTextView, TextView weatherLocation, TextView[] day, TextView[] maxTempDay, TextView[] minTempDay, ImageView[] iconDay) {
    this.context=context;
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
                statusTextView.setText(R.string.cannot_get_weather);
                iconImageView.setImageDrawable(null);
            }
        });
    }
}
