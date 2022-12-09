package com.main.dhbworld.Calendar;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEntity;
import java.util.ArrayList;
import java.util.Calendar;

public class EventWStyle {
    long id;
    String title;
    Calendar startTime;
    Calendar endTime;
    String description;
    WeekViewEntity.Style style;

    EventWStyle(long id, String title, Calendar startTime, Calendar endTime, String description) {
        this.id = id;
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

    public void setStyle(String color){
    style = new WeekViewEntity.Style.Builder().setBackgroundColor(color.hashCode()).build(); }

}
