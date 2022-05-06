package com.main.dhbworld;

public class Course {
    String name;
    int year;
    String study;
    String roomNo;


//    public Course(String name, int year, String study, String roomNo) {
//        this.name = name;
//        this.year = year;
//        this.study = study;
//        this.roomNo = roomNo;
//    }

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
