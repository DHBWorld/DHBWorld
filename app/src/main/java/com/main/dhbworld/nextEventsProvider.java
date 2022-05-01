package com.main.dhbworld;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.exceptions.NoConnectionException;
import dhbw.timetable.rapla.parser.DataImporter;

public class nextEventsProvider {
    SharedPreferences preferences = getSharedPreferences();
    //String url = preferences.getString("CurrentURL",null);
    String url = "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=eisenbiegler&file=TINF20B4";
    Calendar cal;

    public SharedPreferences getSharedPreferences(){
        CalendarActivity calendarActivity= new CalendarActivity();
        return PreferenceManager.getDefaultSharedPreferences(calendarActivity);
    }

    public Event getNextEvent() throws NoConnectionException, IllegalAccessException, MalformedURLException {
        cal  = nextMonday();
        System.out.println(cal);
        Calendar nextWeekCal = (Calendar) cal.clone();
        nextWeekCal.add(Calendar.WEEK_OF_YEAR,1);
        LocalDate nextWeek = LocalDateTime.ofInstant(nextWeekCal.toInstant(), ZoneId.systemDefault()).toLocalDate();
        Calendar mondayCal = (Calendar) cal.clone();
        mondayCal.set(Calendar.DAY_OF_WEEK,
                mondayCal.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);
        LocalDate thisWeek = LocalDateTime.ofInstant(mondayCal.toInstant(), ZoneId.systemDefault()).toLocalDate();

        Map<LocalDate, ArrayList<Appointment>> rawData = DataImporter.ImportWeekRange(thisWeek,nextWeek,url);
        System.out.println(thisWeek + "    " + rawData);
        ArrayList<Appointment> thisWeekData = rawData.get(thisWeek);
        return(disectData(thisWeekData));
    }

    public Calendar nextMonday(){
       Calendar mondayCal = Calendar.getInstance();
        if(mondayCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || mondayCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            mondayCal.add(Calendar.DAY_OF_WEEK,2);
        }
       return mondayCal;
    }

    public Event disectData(ArrayList<Appointment> data){
        Calendar thisWeekCal = cal;
        LocalDateTime thisWeek = LocalDateTime.ofInstant(thisWeekCal.toInstant(), ZoneId.systemDefault());
        Appointment nextEvent = data.get(0);
            for(int i = 0; i < data.size(); i++){
                Appointment currentEvent = data.get(i);
                if(currentEvent.getStartDate().isAfter(thisWeek)
                && currentEvent.getStartDate().isBefore(nextEvent.getStartDate())){
                    nextEvent = currentEvent;
                }
            }
            return(formatAppointment(nextEvent));
    }

    public Event formatAppointment(Appointment appointment){
        Event event = new Event(
                appointment.getStartDate(),
                appointment.getEndDate(),
                appointment.getPersons(),
                appointment.getResources(),
                appointment.getTitle(),
                appointment.getInfo());
        return  event;
    }
}
