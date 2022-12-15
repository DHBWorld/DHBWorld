package com.main.dhbworld.Calendar;

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
    static Map<String, Integer> colorMap;
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

            cacheData();
            styleAndFilter();
        }
    }

    public static void cacheData(){
        // Get a reference to the SharedPreferences object
        SharedPreferences.Editor editor = preferences.edit();

        // Convert the eventList to a JSON string using Gson
        Gson gson = new Gson();
        String eventString = gson.toJson(eventList);

        // Save the JSON string in the SharedPreferences object under the key "CalendarData"
        editor.putString("CalendarData", eventString);

        // Save the changes to the SharedPreferences object
        editor.apply();
    }

    public static void getCachedData(){
        // Retrieve the JSON string from the SharedPreferences object using the key "CalendarData"
        String eventString = preferences.getString("CalendarData", null);

        // Use Gson to convert the JSON string back into a list of Events objects
        Gson gson = new Gson();
        Type eventType = new TypeToken<ArrayList<Events>>() {}.getType();
        eventList = gson.fromJson(eventString,eventType);

        // Apply some styling and filtering to the eventList
        styleAndFilter();
    }




    // Pass each Event into the EventWStyle class while also applying the users filter
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

    public static int setEventColor(int i){
        Random rnd = new Random();

        if(eventList.get(i).getEndTime().get(Calendar.HOUR_OF_DAY) - eventList.get(i).getStartTime().get(Calendar.HOUR_OF_DAY) >= 8){
            return Color.BLUE;
        }
        else if(eventList.get(i).getTitle().toLowerCase().contains("klausur")){
            return 0xFFcf0606;
        }
        else if(eventList.get(i).getEndTime().before(Calendar.getInstance())){
            return 0xFF968686;
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
        int lessTransparentColor = (ColorUtils.blendARGB(white, rndColor, 0.8F));
        colorMap.put(eventList.get(i).getTitle(), lessTransparentColor);
        return lessTransparentColor;
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
