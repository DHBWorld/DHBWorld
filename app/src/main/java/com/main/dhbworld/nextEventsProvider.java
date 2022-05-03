package com.main.dhbworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.exceptions.NoConnectionException;
import dhbw.timetable.rapla.parser.DataImporter;

public class nextEventsProvider {
    private String url;
    Map<LocalDate, ArrayList<Appointment>> rawData;
    Context context;
    SharedPreferences sp;

    public nextEventsProvider(Context context){
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Appointment getNextEvent() throws NoConnectionException, IllegalAccessException, MalformedURLException {
        url = getUrl();
        Calendar thisWeekCal = Calendar.getInstance();
        Calendar nextWeekCal = (Calendar) thisWeekCal.clone();
        nextWeekCal.add(Calendar.WEEK_OF_YEAR,2);
        LocalDate nextWeek = LocalDateTime.ofInstant(nextWeekCal.toInstant(), ZoneId.systemDefault()).toLocalDate();
        Calendar mondayCal = (Calendar) thisWeekCal.clone();
        mondayCal.set(Calendar.DAY_OF_WEEK,
                mondayCal.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);
        LocalDate thisWeek = LocalDateTime.ofInstant(mondayCal.toInstant(), ZoneId.systemDefault()).toLocalDate();

        Map<LocalDate, ArrayList<Appointment>> rawData = DataImporter.ImportWeekRange(thisWeek,nextWeek,url);
        System.out.println(thisWeek + "    " + rawData);
        ArrayList<Appointment> thisWeekData = rawData.get(thisWeek);
        return(disectData(thisWeekData));
    }

    public Appointment disectData(ArrayList<Appointment> data){
        Calendar thisWeekCal = Calendar.getInstance();
        LocalDateTime thisWeek = LocalDateTime.ofInstant(thisWeekCal.toInstant(), ZoneId.systemDefault());
        Appointment nextEvent = data.get(0);
            for(int i = 0; i < data.size(); i++){
                Appointment currentEvent = data.get(i);
                if(currentEvent.getEndDate().isAfter(thisWeek)
                && currentEvent.getStartDate().isBefore(nextEvent.getStartDate())){
                    nextEvent = currentEvent;
                }
            }
            return(nextEvent);
    }

    public String getUrl(){
        return sp.getString("CurrentURL",null);
    }
}
