package com.main.dhbworld;

import java.util.Calendar;

public class Event {

  private final Calendar startDate;
  private final Calendar endDate;
  private final String person;
  private final String resource;
  private String title;

    public Event(Calendar startDate, Calendar endDate, String person, String resource, String title) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.person = person;
        this.resource = resource;
        this.title = title;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public long getId(){
        String allString = title + startDate.toString() + resource;
        return allString.hashCode();
    }
}
