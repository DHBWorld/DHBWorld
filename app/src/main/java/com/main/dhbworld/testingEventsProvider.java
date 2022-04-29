package com.main.dhbworld;

import java.net.MalformedURLException;

import dhbw.timetable.rapla.exceptions.NoConnectionException;

public class testingEventsProvider {
    nextEventsProvider provider = new nextEventsProvider();

    public void test1() throws IllegalAccessException, MalformedURLException, NoConnectionException {
        System.out.println(provider.getNextEvent());
    }
}
