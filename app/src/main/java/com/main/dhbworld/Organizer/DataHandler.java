package com.main.dhbworld.Organizer;

import java.util.ArrayList;
import java.util.Locale;

abstract class DataHandler {
    abstract ArrayList filter(String query);
}

class CourseDataHandler extends DataHandler{
    ArrayList<Course> list;

    public CourseDataHandler(ArrayList<Course> list){ this.list = new ArrayList<>(list); }

    @Override
    ArrayList<Course> filter(String query) {
        ArrayList<Course> newCourses = new ArrayList<>();
        query = query.toLowerCase();
        if(query.isEmpty() | query.length() == 0){
            newCourses = list;
        }
        else{
            for(Course course: list){
                if(course.filterString().toLowerCase().contains(query)){
                    newCourses.add(course);
                }
            }
        }
        return newCourses;
    }

    public void setList(ArrayList<Course> list) {
        this.list = new ArrayList<>(list);
    }
}

class PersonDataHandler extends DataHandler{
    ArrayList<Person> list;

    public PersonDataHandler(ArrayList<Person> list){
        this.list = new ArrayList<>(list);
    }

    @Override
    ArrayList<Person> filter(String query) {
        System.out.println(list.size());
        ArrayList<Person> newPeople = new ArrayList<>();
        query = query.toLowerCase();
        if(query.isEmpty() | query.length() == 0){
            newPeople = list;
        }
        else{
            for(Person person: list){
                if(person.filterString().toLowerCase().contains(query)){
                    newPeople.add(person);
                }
            }
        }
        return newPeople;
    }

    public void setList(ArrayList<Person> list) {
        this.list = new ArrayList<>(list);
    }
}

class RoomsDataHandler extends DataHandler{
    ArrayList<Room> list;

    public RoomsDataHandler(ArrayList<Room> list){
        this.list = new ArrayList<>(list);
    }

    @Override
    ArrayList<Room> filter(String query) {
        ArrayList<Room> newRooms = new ArrayList<>();
        query = query.toLowerCase();
        if(query.isEmpty() | query.length() == 0){
            newRooms = list;
        }
        else{
            for(Room room: list){
                if(room.filterString().toLowerCase(Locale.ROOT).contains(query)){
                    newRooms.add(room);
                }
            }
        }
        return newRooms;
    }

    public void setList(ArrayList<Room> list) {
        this.list = new ArrayList<>(list);
    }
}