package com.main.dhbworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;

import com.alamkanak.weekview.WeekViewEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import dhbw.timetable.rapla.data.event.Appointment;

public class EventCreator {
    static List<String> blackList;
    static ArrayList<Events> filteredEvents;
    static ArrayList<Events> events;
    static Map<String, Integer> colorMap;
   static ArrayList<Event> eventList;
   static ArrayList<Long> uniqueIds;
   static ArrayList<String> filterTitles;


   public static void instantiateVariables(){
       blackList = new ArrayList<>();
       filteredEvents = new ArrayList<>();
       events = new ArrayList<>();
       colorMap = new HashMap<>();
       eventList = new ArrayList<>();
       uniqueIds = new ArrayList<>();
       filterTitles = new ArrayList<>();
   }

    public static void fillData(Map<LocalDate, ArrayList<Appointment>> data, Calendar date){


        LocalDate asLocalDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        ArrayList<Appointment> currentData = data.get(asLocalDate);
        for (int i = 0; i < currentData.size();i++){
            String resources = currentData.get(i).getResources().replace(",","\n");
            filterTitles.add(currentData.get(i).getTitle());
            eventList.add(new Event(
                    localDateTimeToDate(currentData.get(i).getStartDate()),
                    localDateTimeToDate(currentData.get(i).getEndDate()),
                    currentData.get(i).getPersons(),
                    resources,
                    currentData.get(i).getTitle(),
                    currentData.get(i).getInfo()));
        System.out.println(eventList.get(i).getTitle() + eventList.get(i).getStartDate().getWeekYear());
        }

        createEvents();
        eventList.clear();
    }

    public static void setBlackList(ArrayList<String> blackList){
        EventCreator.blackList = blackList;
    }

    public static void applyBlackList(){
        blackList = updateBlackList();
        filteredEvents.clear();
        for(int i = 0; i < events.size(); i++){
            if(!blackList.contains(events.get(i).title)){
                filteredEvents.add(events.get(i));
            }
        }
        CalendarActivity.setEvents(filteredEvents);
    }

    public static ArrayList<String> updateBlackList(){
        return CalendarActivity.getBlackList();
    }

    public static void createEvents(){
        for(int i = 0; i< eventList.size(); i++){
            long id = eventList.get(i).getId();
                if(!uniqueIds.contains(id)) {
                    uniqueIds.add(id);
                    Events event = new Events(
                            id,
                            eventList.get(i).title,
                            eventList.get(i).startDate,
                            eventList.get(i).endDate,
                            eventList.get(i).person + ", " +
                                    eventList.get(i).resource,
                            setEventColor(i));
                    events.add(event);

                }
        }
       applyBlackList();
    }

    public static WeekViewEntity.Style setEventColor(int i) throws NullPointerException{
        WeekViewEntity.Style style = null;
        Random rnd = new Random();

        if(eventList.get(i).endDate.get(Calendar.HOUR_OF_DAY) - eventList.get(i).startDate.get(Calendar.HOUR_OF_DAY) >= 8){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#86c5da")).build();
        }
        else if(eventList.get(i).title.contains("Klausur")){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.RED).build();
        }
        else if(eventList.get(i).endDate.before(Calendar.getInstance())){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#A9A9A9")).build();
        }
        else if(colorMap.containsKey(eventList.get(i).title)){
            try { style = new WeekViewEntity.Style.Builder().setBackgroundColor(colorMap.get(eventList.get(i).title)).build(); }
            catch (Exception e){ e.printStackTrace(); }
        }
        else {
            final int white = Color.WHITE;
            final int red = (rnd.nextInt(130) + 20);
            final int green = (rnd.nextInt(150) + 20);
            final int blue = (rnd.nextInt(190) + 20);
            int rndColor = Color.rgb(red, green, blue);
            int mix = ColorUtils.blendARGB(white, rndColor, 0.8F);
            colorMap.put(eventList.get(i).title, mix);
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

    public static List<String> getBlackList() {
        return blackList;
    }
    public static ArrayList<Events> getEvents() {
        return filteredEvents;
    }
    public static ArrayList<Event> getEventList(){ return eventList; }

    public static void clearEvents(){
        filteredEvents.clear();
        eventList.clear();
        events.clear();
    }
}
