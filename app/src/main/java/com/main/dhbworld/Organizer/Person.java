package com.main.dhbworld.Organizer;

public class Person {
    String name;
    String field;
    String study;
    String email;
    String phoneNumber;
    String roomNo;


    public Person() { }

    public String filterString(){return name + field + study + email + phoneNumber + roomNo;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setField(String field) {
        this.field = field;
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

    public String getStudy() {
        return study;
    }


}
