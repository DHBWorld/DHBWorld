package com.main.dhbworld.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.dhbworld.NetworkAvailability;
import com.main.dhbworld.R;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;

public class nextEventsProvider {
    Context context;
    SharedPreferences sp;
    static ArrayList<String> blackList = new ArrayList<>();

    public nextEventsProvider(Context context){
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Appointment getNextEvent(){
        String eventString = sp.getString("CalendarData", null);
        if (NetworkAvailability.check(context)) {
            try {


                String url = getUrl();
                blackList = getBlackList(context);
                Calendar thisWeekCal = Calendar.getInstance();
                Calendar nextWeekCal = (Calendar) thisWeekCal.clone();
                nextWeekCal.add(Calendar.WEEK_OF_YEAR, 2);
                LocalDate nextWeek = LocalDateTime.ofInstant(nextWeekCal.toInstant(), ZoneId.systemDefault()).toLocalDate();
                Calendar mondayCal = (Calendar) thisWeekCal.clone();
                mondayCal.set(Calendar.DAY_OF_WEEK,
                        mondayCal.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);
                LocalDate thisWeek = LocalDateTime.ofInstant(mondayCal.toInstant(), ZoneId.systemDefault()).toLocalDate();

                Map<LocalDate, ArrayList<Appointment>> rawData = DataImporter.ImportWeekRange(thisWeek, nextWeek, url);
                return (disectData(mergeData(rawData)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if(!eventString.isEmpty()){
            ArrayList<Events> eventList;
            // Use Gson to convert the JSON string back into a list of Events objects
            Gson gson = new Gson();
            Type eventType = new TypeToken<ArrayList<Events>>() {}.getType();
            eventList = gson.fromJson(eventString,eventType);
            ArrayList<Appointment> appointmentList = new ArrayList<>();

            for (Events event : eventList) {
                LocalDateTime start = event.getStartTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                LocalDateTime end = event.getEndTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                Appointment appointment = new Appointment(
                        start,
                        end,
                        event.getTitle(),
                        "",
                        event.getDescription()
                );
                appointmentList.add(appointment);
            }
            return disectData(appointmentList);
        }
        return null;
    }

    //merge all the ArrayLists of incoming Map into single ArrayList
    public ArrayList<Appointment> mergeData(Map<LocalDate, ArrayList<Appointment>> rawData){
        ArrayList<Appointment> allAppointments = new ArrayList<>();
        Collection<ArrayList<Appointment>> a = rawData.values();
        for (ArrayList<Appointment> b : a){
            allAppointments.addAll(b);
        }
        return allAppointments;
    }

    public Appointment disectData(ArrayList<Appointment> data){
        ArrayList<Appointment> futureEvents = getFutureEvents(data);
        if(futureEvents == null | Objects.requireNonNull(futureEvents).isEmpty()){
            return new Appointment(null,null,context.getString(R.string.no_classes),"","");
        }
        Appointment nextEvent = futureEvents.get(0);
            for(int i = 0; i < futureEvents.size(); i++){
                if(futureEvents.get(i).getStartDate().isBefore(nextEvent.getStartDate())){
                    nextEvent = futureEvents.get(i);
                }
            }
            return(nextEvent);
    }

    public ArrayList<Appointment> getFutureEvents(ArrayList<Appointment> data){
        ArrayList<Appointment> futureEvents = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            if(data.get(i).getEndDate().isAfter(LocalDateTime.now()) &&
                    !blackList.contains(data.get(i).getTitle())){
                futureEvents.add(data.get(i));
            }
        }
        return futureEvents;
    }

    public ArrayList<String> getBlackList(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("blackList", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> blackList = gson.fromJson(json, type);
        if (blackList == null){
            return new ArrayList<>();
        }
        else{
            return blackList;
        }
    }

    public String getUrl(){
        return sp.getString("CurrentURL",null);
    }
}
