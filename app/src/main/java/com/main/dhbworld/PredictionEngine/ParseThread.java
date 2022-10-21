package com.main.dhbworld.PredictionEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;

public class ParseThread implements Runnable {
    String url;
    ArrayList<Appointment> temp;

    public ParseThread(String url){
        this.url = url;
    }

    @Override
    public void run() {
        Calendar date = Calendar.getInstance();
        LocalDate today = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        try {
            temp = DataImporter.ImportDay(today,url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Appointment> getTemp(){
        return temp;
    }
    }
