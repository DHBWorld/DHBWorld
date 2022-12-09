package com.main.dhbworld.Calendar;

import com.alamkanak.weekview.WeekViewEntity;
import java.util.Calendar;

public class EventWStyle {
    long id;
    String title;
    Calendar startTime;
    Calendar endTime;
    String description;
    WeekViewEntity.Style style;

    EventWStyle(long id, String title, Calendar startTime, Calendar endTime, String description, String styleString) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.style = new WeekViewEntity.Style.Builder().setBackgroundColor(styleString.hashCode()).build();
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

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    }

