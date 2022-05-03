package com.main.dhbworld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alamkanak.weekview.WeekViewEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;


@RunWith(AndroidJUnit4.class)
public class EventCreatorTest {
    private EventCreator eventCreator;
    Map<LocalDate, ArrayList<Appointment>> data = new HashMap<>();

    @Before
    public void setUp(){
        eventCreator = new EventCreator();
    }

//    @Test
//    public void setEventColorTest(){
//        ArrayList<Event> tempList = new ArrayList<>();
//        Calendar start = Calendar.getInstance();
//        Calendar end = Calendar.getInstance();
//        end.add(Calendar.HOUR_OF_DAY,1);
//
//        tempList.add(new Event(start,end,"testPerson","testResource","testTitleKlausur","testInfo"));
//        EventCreator.eventList = tempList;
//        WeekViewEntity.Style outcomeStyle = EventCreator.setEventColor(0);
//        WeekViewEntity.Style expectedStyle = new WeekViewEntity.Style.Builder().setBackgroundColor(R.color.red).build();
//
//        Events events = new Events(1,"1",start,end,"a",outcomeStyle);
//        Events events2 = new Events(1,"1",start,end,"a",expectedStyle);
//
//        assertEquals(events,events2);
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

    @Test
    public void createEventsTest(){
        Calendar cal = Calendar.getInstance();
        cal.set(2022,5,2,12,0,0);
        EventCreator.fillData(data,cal);


        assertEquals(EventCreator.getEvents().size(),12);
    }

    Runnable getData = () -> {
        Calendar cal = Calendar.getInstance();
        cal.set(2022,5,2,12,0,0);
        LocalDate thisWeek = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault()).toLocalDate();
        Calendar dateCopy = (Calendar) cal.clone();
        dateCopy.add(Calendar.WEEK_OF_YEAR,1);
        LocalDate nextWeek = LocalDateTime.ofInstant(dateCopy.toInstant(), ZoneId.systemDefault()).toLocalDate();

        try {
            data = DataImporter.ImportWeekRange(thisWeek,nextWeek, "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=eisenbiegler&file=TINF20B4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

}

