package com.main.dhbworld.Weather;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.main.dhbworld.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class WeatherData {

   private double currentTemperature;
   private boolean isDay;
   private final ArrayList<Forecast> forecasts = new ArrayList<>();

   private int weatherCode;

   public double getCurrentTemperature() {
      return currentTemperature;
   }

   public int getCurrentTemperatureRounded() {
      return (int) Math.round(currentTemperature);
   }

   protected void setCurrentTemperature(double currentTemperature) {
      this.currentTemperature = currentTemperature;
   }

   public boolean isDay() {
      return isDay;
   }

   protected void setDay(boolean day) {
     this.isDay = day;
   }

   protected void setDay(int day) {
      this.isDay = day == 1;
   }

   protected void addForecast(Forecast forecast) {
      this.forecasts.add(forecast);
   }

   public ArrayList<Forecast> getForecasts() {
      return this.forecasts;
   }

   protected void setWeatherCode(int weatherCode) {
      this.weatherCode = weatherCode;
   }

   public int getWeatherCode() {
      return this.weatherCode;
   }

   public Drawable getIcon(Context context) {
      try {
         String[] icons = context.getAssets().list("weather/");
         String file = "weather/" + weatherCode + ".png";
         if (!isDay) {
            if (Arrays.asList(icons).contains(weatherCode + "-night.png")) {
               file = "weather/" + weatherCode + "-night.png";
            }
         }

         if (!Arrays.asList(icons).contains(file.substring(file.indexOf("/")+1))) {
            file = "weather/3.png";
         }

         InputStream ims = context.getAssets().open(file);
         Drawable drawable = Drawable.createFromStream(ims, null);
         ims.close();
         return drawable;
      } catch (IOException e) {
         return null;
      }
   }

   public String getTranslatedWeatherCode(Context context) {
      return getTranslatedWeatherCode(this.weatherCode, context);
   }

   public static String getTranslatedWeatherCode(int weatherCode, Context context) {
      int stringId;
      switch (weatherCode) {
         case 0:
            stringId = R.string.clear_sky;
            break;
         case 1:
            stringId = R.string.mainly_clear;
            break;
         case 2:
            stringId = R.string.partly_cloudy;
            break;
         case 3:
            stringId = R.string.overcast;
            break;
         case 45:
            stringId = R.string.fog;
            break;
         case 48:
            stringId = R.string.depositing_rime_fog;
            break;
         case 51:
            stringId = R.string.light_drizzle;
            break;
         case 53:
            stringId = R.string.moderate_drizzle;
            break;
         case 55:
            stringId = R.string.dense_drizzle;
            break;
         case 56:
            stringId = R.string.light_freezing_drizzle;
            break;
         case 57:
            stringId = R.string.dense_freezing_drizzle;
            break;
         case 61:
            stringId = R.string.slight_rain;
            break;
         case 63:
            stringId = R.string.moderate_rain;
            break;
         case 65:
            stringId = R.string.heavy_rain;
            break;
         case 66:
            stringId = R.string.light_freezing_rain;
            break;
         case 67:
            stringId = R.string.heavy_freezing_rain;
            break;
         case 71:
            stringId = R.string.slight_snowfall;
            break;
         case 73:
            stringId = R.string.moderate_snowfall;
            break;
         case 75:
            stringId = R.string.heavy_snowfall;
            break;
         case 77:
            stringId = R.string.snow_grains;
            break;
         case 80:
            stringId = R.string.slight_rain_showers;
            break;
         case 81:
            stringId = R.string.moderate_rain_showers;
            break;
         case 82:
            stringId = R.string.violent_rain_showers;
            break;
         case 85:
            stringId = R.string.slight_snow_showers;
            break;
         case 86:
            stringId = R.string.heavy_snow_showers;
            break;
         case 95:
            stringId = R.string.slight_thunderstorm;
            break;
         case 96:
            stringId = R.string.thunderstorm_with_slight_hail;
            break;
         case 97:
            stringId = R.string.heavy_thunderstorm;
            break;
         case 99:
            stringId = R.string.thunderstorm_with_heavy_hail;
            break;
         default:
            stringId = R.string.unknown_condition;
            break;
      }

      return context.getResources().getString(stringId);
   }
}
