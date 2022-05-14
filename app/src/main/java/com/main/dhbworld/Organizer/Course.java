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

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", study='" + study + '\'' +
                ", roomNo='" + roomNo + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getStudy() {
        return study;
    }

    public String getRoomNo() {
        return roomNo;
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

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

}
