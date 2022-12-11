package com.main.dhbworld.Calendar;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
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
import java.util.Set;

import dhbw.timetable.rapla.data.event.Appointment;

public class EventCreator {
    static List<String> blackList;
    static ArrayList<Events> newEventList;
    static Map<String, String> colorMap;
   static ArrayList<Events> eventList;
   static ArrayList<String> filterTitles;
   static SharedPreferences preferences;
    static ArrayList<EventWStyle> styledEvents;
    static ArrayList<Set<LocalDate>> currentlyLoaded;

    public static void instantiateVariables(Context context){
       preferences = PreferenceManager.getDefaultSharedPreferences(context);
       blackList = new ArrayList<>();
       newEventList = new ArrayList<>();
       colorMap = new HashMap<>();
       eventList = new ArrayList<>();
       styledEvents = new ArrayList<>();
       filterTitles = new ArrayList<>();
       currentlyLoaded = new ArrayList<>();
   }

    // Fills data for a given date into the appropriate variables
    public static void fillData(Map<LocalDate, ArrayList<Appointment>> data, Calendar date){
        // Get the set of keys (dates) in the data map
        Set<LocalDate> keySet = data.keySet();

        // Check if the currently loaded data contains the given date
        if(!currentlyLoaded.contains(keySet)){
            // Convert the given Calendar date to a LocalDate
            LocalDate asLocalDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();

            // Get the list of appointments for the given date
            ArrayList<Appointment> currentData = data.get(asLocalDate);

            // Iterate through the list of appointments
            for (int i = 0; i < Objects.requireNonNull(currentData).size(); i++) {
                // Add the title of the current appointment to the filterTitles list
                filterTitles.add(currentData.get(i).getTitle());

                // Create a new event using the data from the current appointment
                Events newEvent = new Events(currentData.get(i).getTitle(),
                        localDateTimeToDate(currentData.get(i).getStartDate()),
                        localDateTimeToDate(currentData.get(i).getEndDate()),
                        currentData.get(i).getPersons() + ", " +
                                currentData.get(i).getResources().replace(",", "\n"));

                // Generate an ID for the new event
                newEvent.createId();

                // Add the new event to the eventList
                eventList.add(newEvent);
            }
            // Add the current data to the currentlyLoaded list
            currentlyLoaded.add(keySet);


            compareListSizes();



            cacheData();
            styleAndFilter();
        }
    }

    // A debugging method that compares the size of all lists in the EventCreator class
    public static void compareListSizes(){
        // Get the sizes of all the lists
        int blackListSize = blackList.size();
        int newEventListSize = newEventList.size();
        int colorMapSize = colorMap.size();
        int eventListSize = eventList.size();
        int filterTitlesSize = filterTitles.size();
        int styledEventsSize = styledEvents.size();
        int currentlyLoadedSize = currentlyLoaded.size();

        // Compare the sizes of the lists and print the result
        if(blackListSize == newEventListSize && newEventListSize == colorMapSize && colorMapSize == eventListSize &&
                eventListSize == filterTitlesSize && filterTitlesSize == styledEventsSize && styledEventsSize == currentlyLoadedSize){
            System.out.println("All lists have the same size: " + blackListSize);
        }
        else{
            System.out.println("List sizes are not equal!");
            System.out.println("blackListSize: " + blackListSize);
            System.out.println("newEventListSize: " + newEventListSize);
            System.out.println("colorMapSize: " + colorMapSize);
            System.out.println("eventListSize: " + eventListSize);
            System.out.println("filterTitlesSize: " + filterTitlesSize);
            System.out.println("styledEventsSize: " + styledEventsSize);
            System.out.println("currentlyLoadedSize: " + currentlyLoadedSize);
        }
    }


    public static void cacheData(){
        SharedPreferences.Editor editor = preferences.edit();
        //convert to string using gson
        Gson gson = new Gson();
        String eventString = gson.toJson(eventList);
        //save string in sharedpreferences as "hashMapString"
        editor.putString("CalendarData", eventString);
        editor.apply();
    }

    public static void getCachedData(){
        String eventString = preferences.getString("CalendarData", null);
        Gson gson = new Gson();
        Type eventType = new TypeToken<ArrayList<Events>>() {}.getType();
        eventList = gson.fromJson(eventString,eventType);
    //  eventList.clear();
        styleAndFilter();
    }

    public static void styleAndFilter(){
        // Clear the styledEvents list before adding new events to it
        styledEvents.clear();
        updateBlackList();
        for(int i = 0; i < eventList.size(); i++) {
            setEventColor(i);
            if (!blackList.contains(eventList.get(i).title)) {
                styledEvents.add(new EventWStyle(
                        eventList.get(i).id,
                        eventList.get(i).title,
                        eventList.get(i).startTime,
                        eventList.get(i).endTime,
                        eventList.get(i).description,
                        setEventColor(i)
                ));
            }
        }
        CalendarActivity.setEvents(styledEvents);
    }


    public static void setBlackList(ArrayList<String> blackList){
        EventCreator.blackList = blackList;
    }

    public static void updateBlackList(){
         blackList = CalendarActivity.getBlackList();
    }

    @SuppressLint("ResourceAsColor")
    public static String setEventColor(int i) throws NullPointerException{
        Random rnd = new Random();

        if(eventList.get(i).getEndTime().get(Calendar.HOUR_OF_DAY) - eventList.get(i).getStartTime().get(Calendar.HOUR_OF_DAY) >= 8){
            return "#86c5da";
        }
        else if(eventList.get(i).getTitle().toLowerCase().contains("klausur")){
            return "E2001A";
        }
        else if(eventList.get(i).getEndTime().before(Calendar.getInstance())){
            return "A9A9A9";
        }
        else if(colorMap.containsKey(eventList.get(i).getTitle())){
            try {
                return Objects.requireNonNull(colorMap.get(eventList.get(i).getTitle()));}
            catch (Exception e){ e.printStackTrace(); }
        }
        final int white = Color.WHITE;
        final int red = (rnd.nextInt(130) + 20);
        final int green = (rnd.nextInt(150) + 20);
        final int blue = (rnd.nextInt(190) + 20);
        int rndColor = Color.rgb(red, green, blue);
        String colorString = String.valueOf(ColorUtils.blendARGB(white, rndColor, 0.8F));
        colorMap.put(eventList.get(i).getTitle(), colorString);
        return colorString;
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
        newEventList.clear();
        eventList.clear();
        styledEvents.clear();
    }
    public static ArrayList<EventWStyle> getEvents() {
       return styledEvents;
    }
}
