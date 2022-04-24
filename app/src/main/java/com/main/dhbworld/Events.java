package com.main.dhbworld;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEntity;

import java.util.Calendar;

public class Events {
    long id;
    String title;
    Calendar startTime;
    Calendar endTime;
    String description;
    WeekViewEntity.Style style;

    Events(long id, String title, Calendar startTime, Calendar endTime, String description, WeekViewEntity.Style style) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.style = style;

    }


}
