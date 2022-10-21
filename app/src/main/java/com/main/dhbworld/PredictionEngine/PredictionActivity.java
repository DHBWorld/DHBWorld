package com.main.dhbworld.PredictionEngine;

import android.content.Context;

import com.main.dhbworld.Organizer.Course;
import com.main.dhbworld.Organizer.OrganizerParser;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


public class PredictionActivity {
    Map<String, ArrayList> entryMap;
    ArrayList<Course> courses = new ArrayList<>();
    Context context;
    ArrayList<Map<LocalDate, ArrayList<Appointment>>> data = new ArrayList<>();
    ArrayList<Appointment> squashedAppointsments = new ArrayList<>();
    String url;

    public PredictionActivity(Context context) {
        this.context = context;
    }

    public void begin() {
        t1.start();
        try {
            ArrayList<String> urls = new ArrayList<>();
            t1.join();
            courses = entryMap.get("courses");
            for (Course course : courses) {
                if (course.getUrl() != null) {
                    urls.add(course.getUrl());
                }
            }
            parseUrls(urls);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    Thread t1 = new Thread(new Runnable() {
        @Override
        public void run() {
            OrganizerParser organizerParser = new OrganizerParser();
            entryMap = organizerParser.getAllElements(context);
        }
    });

    public void parseUrls(ArrayList<String> urls) {
        Thread a = null;
        for (String url : urls) {
            this.url = url;
            a = new Thread(t2);
            a.start();
        }
        while(a.isAlive()){
            if(!a.isAlive()){
                calc(squashedAppointsments);
            }
        }
    }

    Thread t2 = new Thread(new Runnable() {
        @Override
        public void run() {
            Calendar date = Calendar.getInstance();
            LocalDate today = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
            try {
                ArrayList<Appointment> a = DataImporter.ImportDay(today,url);
                squashedAppointsments.addAll(a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });


    public void calc(ArrayList<Appointment> aps){
        int avgStart = 0;
        int avgEnd = 0;

        int ten = 0;
        int eleven = 0;
        int twelve = 0;
        int thirteen = 0;
        int fourteen = 0;
        int error = 0;



        for(Appointment a : aps){

                avgStart += a.getStartDate().getHour();
                avgEnd += a.getEndDate().getHour();

                switch (a.getEndDate().getHour()){
                    case 10:
                        ten++;
                        break;
                    case 11:
                        eleven++;
                        break;
                    case 12:
                        twelve++;
                        break;
                    case 13:
                        thirteen++;
                        break;
                    case 14:
                        fourteen++;
                        break;
                    default:
                        error++;
               }

        }

        /*

        10: Wie viele Vorlesungen haben bereits um 10 aufgehört?
        11: Wie
        12
        13


         */





        avgStart = avgStart / aps.size();
        avgEnd = avgEnd / aps.size();
        LocalDate d = aps.get(0).getStartDate().toLocalDate();
        System.out.println("Average starting time for " + d.getDayOfMonth() + ". " + d.getMonth() + " is: " + avgStart);
        System.out.println("Average end time for " + d.getDayOfMonth() + ". " + d.getMonth() + " is: " + avgEnd);
        System.out.println("Number of Events Ending at given times. \n Ten: " + ten + "\n + Eleven: " + eleven + "\n + Twelve: " + twelve + "\n + Thirteen: " + thirteen + "\n + Fourteen: " + fourteen + " \n + Errors: " + error);
    }
}