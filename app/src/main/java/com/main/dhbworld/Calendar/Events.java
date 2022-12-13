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

    Events(String title, Calendar startTime, Calendar endTime, String description) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

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

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void createId(){
       id = (title + startTime.toString() + description).hashCode();
    }

    public String getDescription() {
        return description;
    }
}
