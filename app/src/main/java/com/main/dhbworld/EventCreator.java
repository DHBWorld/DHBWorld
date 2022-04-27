package com.main.dhbworld;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.alamkanak.weekview.WeekViewEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import dhbw.timetable.rapla.data.event.Appointment;

public class EventCreator {
    static List<Calendar> startDateList = new ArrayList<>();
    static List<Calendar> endDateList = new ArrayList<>();
    static List<String> personList = new ArrayList<>();
    static List<String> resourceList = new ArrayList<>();
    static List<String> titleList = new ArrayList<>();
    static List<String> infoList = new ArrayList<>();
    static List<String> blackList = new ArrayList<>();
    static ArrayList<Events> filteredEvents = new ArrayList<>();



    static List<String> allTitleList = new ArrayList<>();
    static ArrayList<Events> events = new ArrayList<>();
    static Map<String, Integer> colorMap = new HashMap<>();

    public static void fillData(Map<LocalDate, ArrayList<Appointment>> data, Calendar date){
        LocalDate asLocalDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        ArrayList<Appointment> currentData = data.get(asLocalDate);
        clearLists();
        for (int i = 0; i <= currentData.size() -1;i++){

            if(!allTitleList.contains(currentData.get(i).getTitle())){
                allTitleList.add(currentData.get(i).getTitle());
            }
            startDateList.add(localDateTimeToDate(currentData.get(i).getStartDate()));
            endDateList.add(localDateTimeToDate(currentData.get(i).getEndDate()));
            personList.add(currentData.get(i).getPersons());
            resourceList.add(currentData.get(i).getResources());
            titleList.add(currentData.get(i).getTitle());

            infoList.add(currentData.get(i).getInfo());
        }
        createEvents();
    }

    public static void setBlackList(ArrayList<String> blackList){
        EventCreator.blackList = blackList;
    }

    public static void applyBlackList(){
        for(int i = 0; i < events.size(); i++){
            if(!blackList.contains(events.get(i).title)){
                filteredEvents.add(events.get(i));
            }
        }
        Events.setEvents(filteredEvents);
    }

    public static void createEvents(){
        for(int i = 0; i< titleList.size() -1; i++){
            String resources = resourceList.get(i).replace(",","\n");
                Random rnd = new Random();
                Events event = new Events(rnd.nextInt(), titleList.get(i), startDateList.get(i),endDateList.get(i), personList.get(i) + ", " + resources, setEventColor(i));
                if (!events.contains(event)) {
                    events.add(event);
                }
        }
       applyBlackList();
    }



    public static WeekViewEntity.Style setEventColor(int i) throws NullPointerException{
        WeekViewEntity.Style style = null;
        Random rnd = new Random();

        if(endDateList.get(i).get(Calendar.HOUR_OF_DAY) - startDateList.get(i).get(Calendar.HOUR_OF_DAY) >= 8){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#86c5da")).build();
        }
        else if(titleList.get(i).contains("Klausur")){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.RED).build();
        }
        else if(endDateList.get(i).before(Calendar.getInstance())){
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#A9A9A9")).build();
        }
        else if(colorMap.containsKey(titleList.get(i))){
            try {
                style = new WeekViewEntity.Style.Builder().setBackgroundColor(colorMap.get(titleList.get(i))).build();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            // liste verschiedener farben, aus denen generiert wird. Am besten deterministisch anhand titels.

            final int white = Color.WHITE;
            final int red = (rnd.nextInt(130) + 20);
            final int green = (rnd.nextInt(150) + 20);
            final int blue = (rnd.nextInt(190) + 20);
            int rndColor = Color.rgb(red, green, blue);
            int mix = ColorUtils.blendARGB(white, rndColor, 0.8F);
            colorMap.put(titleList.get(i), mix);
            style = new WeekViewEntity.Style.Builder().setBackgroundColor(rndColor).build();

        }
        return style;
    }

    public static Calendar localDateTimeToDate(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue()-1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }

    public static void clearLists() {
        startDateList.clear();
        endDateList.clear();
        personList.clear();
        resourceList.clear();
        titleList.clear();
        infoList.clear();
        events.clear();
    }
    public static List<String> getTitleList() {
        return titleList;
    }
    public static List<String> getAllTitleList() {
        return allTitleList;
    }


}
