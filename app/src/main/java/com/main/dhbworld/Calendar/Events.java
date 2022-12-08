package com.main.dhbworld.Calendar;

import android.graphics.Color;

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

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public WeekViewEntity.Style getStyle() {
        return style;
    }

    public void setStyle(WeekViewEntity.Style style) {
        this.style = style;
    }

    Events(long id, String title, Calendar startTime, Calendar endTime, String description) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }
    public static void setEvents(ArrayList<Events> events) {
        Events.events = events;
    }
    public static ArrayList<Events> getEvents() {
        return events;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStyle(String color){
        style = new WeekViewEntity.Style.Builder().setBackgroundColor(color.hashCode()).build();
    }

}
