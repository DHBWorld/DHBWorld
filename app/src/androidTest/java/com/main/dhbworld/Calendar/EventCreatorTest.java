package com.main.dhbworld.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.alamkanak.weekview.WeekViewEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dhbw.timetable.rapla.data.event.Appointment;


@RunWith(AndroidJUnit4.class)
public class EventCreatorTest {
    private EventCreator eventCreator;

    @Before
    public void setUp(){
        eventCreator = new EventCreator();
    }
//
//    @Test
//    public void setEventColorTest(){
//        ArrayList<Event> tempList = new ArrayList<>();
//        Calendar start = Calendar.getInstance();
//        Calendar end = Calendar.getInstance();
//        end.add(Calendar.HOUR_OF_DAY,1);
//        tempList.add(new Event(start,end,"testPerson","testResource","testTitleKlausur"));
//        EventCreator.eventList = tempList;
//        EventCreator.setEventColor(3);
//        assertFalse(outcomeStyle.toString().isEmpty());
//    }

    @Test
    public void localDateTimeToDateTest(){
        LocalDateTime expectedDate = LocalDateTime.now();
        Calendar outcomeDate = EventCreator.localDateTimeToDate(expectedDate);

        assertEquals(expectedDate.getYear(),outcomeDate.get(Calendar.YEAR));
        //Calendar Month starts at 0, LocalDateTime at 1!
        assertEquals(expectedDate.getYear(),outcomeDate.get(Calendar.YEAR));
        assertEquals(expectedDate.getMonthValue(),outcomeDate.get(Calendar.MONTH) + 1);
        assertEquals(expectedDate.getDayOfMonth(),outcomeDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(expectedDate.getHour(),outcomeDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(expectedDate.getMinute(),outcomeDate.get(Calendar.MINUTE));
    }

}

