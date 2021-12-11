package com.main.dhbworld;

import java.util.Calendar;

public class Events {
    long id;
    String title;
    Calendar startTime;
    Calendar endTime;
    String description;

    Events(long id, String title, Calendar startTime, Calendar endTime, String description) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }


}
