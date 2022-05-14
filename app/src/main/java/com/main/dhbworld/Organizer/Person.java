package com.main.dhbworld.Organizer;

public class Person {
    String name;
    String abteilung;
    String study;
    String email;
    String phoneNumber;
    String roomNo;

    public Person(String name, String abteilung, String studiengang, String email, String phoneNumber, String roomNo) {
        this.name = name;
        this.abteilung = abteilung;
        this.study = studiengang;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roomNo = roomNo;
    }

    public Person() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbteilung(String abteilung) {
        this.abteilung = abteilung;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getAbteilung() {
        return abteilung;
    }

    public String getStudy() {
        return study;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRoomNo() {
        return roomNo;
    }
}
