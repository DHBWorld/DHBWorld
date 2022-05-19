package com.main.dhbworld.Calendar;

import com.alamkanak.weekview.WeekViewEntity;

import java.util.ArrayList;
import java.util.Calendar;

public class Events {
    long id;
    String title;
    Calendar startTime;
    Calendar endTime;
    String description;
    WeekViewEntity.Style style;
    static ArrayList<Events> events = new ArrayList<>();

    Events(long id, String title, Calendar startTime, Calendar endTime, String description, WeekViewEntity.Style style) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.style = style;
    }
    public static void setEvents(ArrayList<Events> events) {
        Events.events = events;
    }
    public static ArrayList<Events> getEvents() {
        return events;
    }

}
