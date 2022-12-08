package com.main.dhbworld.Calendar;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;

import com.alamkanak.weekview.WeekViewEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import dhbw.timetable.rapla.data.event.Appointment;

public class EventCreator {
    static List<String> blackList;
    static ArrayList<Events> newEvents;
    static ArrayList<Events> events;
    static Map<String, Integer> colorMap;
   static ArrayList<Event> eventList;
   static ArrayList<Long> uniqueIds;
   static ArrayList<String> filterTitles;
   static SharedPreferences preferences;

    public static void instantiateVariables(Context context){
       preferences = PreferenceManager.getDefaultSharedPreferences(context);
       blackList = new ArrayList<>();
       newEvents = new ArrayList<>();
       events = new ArrayList<>();
       colorMap = new HashMap<>();
       eventList = new ArrayList<>();
       uniqueIds = new ArrayList<>();
       filterTitles = new ArrayList<>();
   }

    //TODO merge fillData and createEvents methods, Event Class is unnecessary
    public static void fillData(Map<LocalDate, ArrayList<Appointment>> data, Calendar date){
        LocalDate asLocalDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        ArrayList<Appointment> currentData = data.get(asLocalDate);
        for (int i = 0; i < Objects.requireNonNull(currentData).size(); i++){
            String resources = currentData.get(i).getResources().replace(",","\n");
            filterTitles.add(currentData.get(i).getTitle());
            eventList.add(new Event(
                    localDateTimeToDate(currentData.get(i).getStartDate()),
                    localDateTimeToDate(currentData.get(i).getEndDate()),
                    currentData.get(i).getPersons(),
                    resources,
                    currentData.get(i).getTitle()));
        }
        createEvents();
    }

    public static void createEvents(){
        for(int i = 0; i< eventList.size(); i++){
            long id = eventList.get(i).getId();
            if(!uniqueIds.contains(id)) {
                uniqueIds.add(id);
                Events event = new Events(
                        id,
                        eventList.get(i).getTitle(),
                        eventList.get(i).getStartDate(),
                        eventList.get(i).getEndDate(),
                        eventList.get(i).getPerson() + ", " +
                                eventList.get(i).getResource());
                events.add(event);
            }
        }
        eventList.clear();
        cacheData();
        applyColorBlacklist();
    }

    public static void cacheData(){
        SharedPreferences.Editor editor = preferences.edit();
        //convert to string using gson
        Gson gson = new Gson();
        String eventString = gson.toJson(events);
        //save string in sharedpreferences as "hashMapString"
        editor.putString("CalendarData", eventString);
        editor.apply();
    }

    public static void getCachedData(){
        String eventString = preferences.getString("CalendarData", null);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WeekViewEntity.Style.class, (InstanceCreator<WeekViewEntity.Style>) type -> {
                    return new WeekViewEntity.Style(); // Initialize and return a new instance of the Style class here.
                })
                .create();

        Type eventType = new TypeToken<ArrayList<Events>>() {}.getType();
        events = gson.fromJson(eventString,eventType);

        eventList.clear();
        applyColorBlacklist();
    }

    public static void setBlackList(ArrayList<String> blackList){
        EventCreator.blackList = blackList;
    }

    public static void applyBlackListCached(){
        blackList = updateBlackList();
        newEvents.clear();
        for(int i = 0; i < events.size(); i++){
            if(!blackList.contains(events.get(i).title)){
                newEvents.add(events.get(i));
            }
        }
        CalendarActivity.setEvents(newEvents);
    }

    public static void applyColorBlacklist(){


        blackList = updateBlackList();
        newEvents.clear();
        for(int i = 0; i < events.size(); i++){
            setEventColor(i);
            if(!blackList.contains(events.get(i).title)){
                newEvents.add(events.get(i));
            }
        }
        CalendarActivity.setEvents(newEvents);
    }

    public static ArrayList<String> updateBlackList(){
        return CalendarActivity.getBlackList();
    }


    @SuppressLint("ResourceAsColor")
    public static WeekViewEntity.Style setEventColor(int i) throws NullPointerException{
        WeekViewEntity.Style style = null;
        Random rnd = new Random();

        if(events.get(i).getEndTime().get(Calendar.HOUR_OF_DAY) - eventList.get(i).getStartDate().get(Calendar.HOUR_OF_DAY) >= 8){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#86c5da")).build();
        }
        else if(eventList.get(i).getTitle().toLowerCase().contains("klausur")){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#E2001A")).build();
        }
        else if(eventList.get(i).getEndDate().before(Calendar.getInstance())){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#A9A9A9")).build();
        }
        else if(colorMap.containsKey(eventList.get(i).getTitle())){
            try { style = new WeekViewEntity.Style.Builder().setBackgroundColor(Objects.requireNonNull(colorMap.get(eventList.get(i).getTitle()))).build(); }
            catch (Exception e){ e.printStackTrace(); }
        }
        else {
            final int white = Color.WHITE;
            final int red = (rnd.nextInt(130) + 20);
            final int green = (rnd.nextInt(150) + 20);
            final int blue = (rnd.nextInt(190) + 20);
            int rndColor = Color.rgb(red, green, blue);
            int mix = ColorUtils.blendARGB(white, rndColor, 0.8F);
            colorMap.put(eventList.get(i).getTitle(), mix);
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(rndColor).build();
        }
        return style;
    }

    public static Calendar localDateTimeToDate(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }

    public static ArrayList<String> uniqueTitles(){
        ArrayList<String> titles = new ArrayList<>();
        for(int i = 0; i < filterTitles.size(); i++){
            String temp = filterTitles.get(i);
            if(!titles.contains(temp)){
                titles.add(temp);
            }
        }
        return titles;
    }

    public static void clearEvents(){
        newEvents.clear();
        eventList.clear();
        events.clear();
    }
    public static ArrayList<Events> getEvents() {
       return newEvents;
    }
}
