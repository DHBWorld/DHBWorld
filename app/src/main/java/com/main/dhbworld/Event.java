package com.main.dhbworld;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {


    Calendar startDate;
    Calendar endDate;
    String person;
    String resource;
    String title;
    String info;
    long id;


    public Event(Calendar startDate, Calendar endDate, String person, String resource, String title, String info) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.person = person;
        this.resource = resource;
        this.title = title;
        this.info = info;
    }

    public Event(LocalDateTime startDate, LocalDateTime endDate, String persons, String resources, String title, String info) {
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public String getPerson() {
        return person;
    }

    public String getResource() {
        return resource;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public long getId(){
        String allString = title + startDate.toString() + resource;
        long id = allString.hashCode();
        return id;
    }


}
