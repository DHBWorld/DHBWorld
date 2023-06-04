package com.main.dhbworld.Weather;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class Forecast {
   final private ZonedDateTime time;
   final private double minTemperature;
   final private double maxTemperature;
   final private int weatherCode;

   public Forecast(ZonedDateTime time, double minTemperature, double maxTemperature, int weatherCode) {
      this.time = time;
      this.minTemperature = minTemperature;
      this.maxTemperature = maxTemperature;
      this.weatherCode = weatherCode;
   }

   public ZonedDateTime getTime() {
      return time;
   }

   public double getMinTemperature() {
      return minTemperature;
   }

   public double getMaxTemperature() {
      return maxTemperature;
   }

   public int getWeatherCode() {
      return weatherCode;
   }

   public Drawable getIcon(Context context) {
      try {
         String[] icons = context.getAssets().list("weather/");
         String file = "weather/" + weatherCode + ".png";

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
}
