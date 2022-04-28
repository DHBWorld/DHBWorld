package com.main.dhbworld;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
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

    Calendar startDate;
    Calendar endDate;
    String person;
    String resource;
    String title;
    String info;


    public Event(Calendar startDate, Calendar endDate, String person, String resource, String title, String info) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.person = person;
        this.resource = resource;
        this.title = title;
        this.info = info;
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
}
