package com.main.dhbworld.Weather;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class WeatherApi {

    public enum City {
        Karlsruhe(49.0068901f, 8.4036527f);

        public final float lat;
        public final float lon;

        City(float lat, float lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    private static final String urlStr = "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m,is_day,weathercode&daily=weathercode,temperature_2m_max,temperature_2m_min&timezone=Europe%2FBerlin";
    private final City city;


    public WeatherApi(City city) {
        this.city = city;
    }

    public void requestData(Context context, WeatherDataListener weatherDataListener) {
        String urlWithCoords = String.format("%s&latitude=%s&longitude=%s", urlStr, this.city.lat, this.city.lon);

        new Thread(() -> {
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithCoords, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject hourly = jsonObject.getJSONObject("hourly");
                    JSONObject daily = jsonObject.getJSONObject("daily");

                    WeatherData weatherData = new WeatherData();

                    JSONArray timestamps = hourly.getJSONArray("time");

                    ZonedDateTime currentTime = Instant.now().atZone(ZoneId.of("Europe/Berlin"));

                    long minDistance = 60*25;

                    for (int i=0; i<timestamps.length(); i++) {
                        ZonedDateTime time = ZonedDateTime.parse(timestamps.getString(i) + ":00.00Z");
                        time = time.withZoneSameLocal(ZoneId.of("Europe/Berlin"));

                        long distance = Math.abs(ChronoUnit.MINUTES.between(time, currentTime));
                        if (distance < minDistance) {
                            minDistance = distance;
                            continue;
                        }

                        System.out.println(i-1);

                        weatherData.setCurrentTemperature(
                                hourly
                                        .getJSONArray("temperature_2m")
                                        .getDouble(Math.max(i-1, 0)));

                        weatherData.setDay(
                                hourly
                                        .getJSONArray("is_day")
                                        .getInt(Math.max(i-1, 0)));

                        weatherData.setWeatherCode(
                                hourly.getJSONArray("weathercode")
                                        .getInt(Math.max(i-1, 0)));
                        break;
                    }

                    timestamps = daily.getJSONArray("time");
                    for (int i=0; i<timestamps.length(); i++) {
                        ZonedDateTime time = ZonedDateTime.parse(timestamps.getString(i) + "T00:00:00.00Z");
                        time = time.withZoneSameLocal(ZoneId.of("Europe/Berlin"));

                        int weathercode = daily.getJSONArray("weathercode").getInt(i);
                        double maxTemperature = daily.getJSONArray("temperature_2m_max").getDouble(i);
                        double minTemperature = daily.getJSONArray("temperature_2m_min").getDouble(i);

                        weatherData.addForecast(new Forecast(time, minTemperature, maxTemperature, weathercode));
                    }

                    weatherDataListener.onSuccess(weatherData);

                } catch (JSONException e) {
                    weatherDataListener.onError();
                }
            }, error -> weatherDataListener.onError());
            queue.add(stringRequest);
        }).start();
    }

    public interface WeatherDataListener {
        void onSuccess(WeatherData weatherData);
        void onError();
    }
}
