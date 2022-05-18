package com.main.dhbworld.Organizer;

public class Course {
    String name;
    int year;
    String study;
    String roomNo;
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String filterString(){
        return name + year + study + roomNo + url;
    }

    public String getName() {
        return name;
    }

    public String getStudy() {
        return study;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setStudy(String study) {
        this.study = study;
    }


}
