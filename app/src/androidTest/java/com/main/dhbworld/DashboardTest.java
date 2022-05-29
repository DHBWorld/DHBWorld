package com.main.dhbworld;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.InstrumentationRegistry;
import com.main.dhbworld.Calendar.nextEventsProvider;
import org.junit.Test;
import java.net.MalformedURLException;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.exceptions.NoConnectionException;

public class DashboardTest {



    @Test
    public void test_saveVisibilityOfCards(){
        Context context = InstrumentationRegistry.getContext();
        SharedPreferences sp = context.getSharedPreferences(DashboardActivity.dashboardSettings,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("cardCalendar", true);
        editor.putBoolean("cardPI", false);
        editor.putBoolean("cardMealPlan", true);
        editor.putBoolean("cardKvv", false);
        editor.apply();

        assertThat(sp.getBoolean("cardCalendar", true), is(true));
        assertThat(sp.getBoolean("cardPI", true), is(false));
        assertThat(sp.getBoolean("cardMealPlan", true), is(true));
        assertThat(sp.getBoolean("cardKvv", true), is(false));

    }



    @Test
    public void test_CalendarData() {
        Context context = InstrumentationRegistry.getContext();
        String url= "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=eisenbiegler&file=TINF20B4";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nextEventsProvider nextEventsProvider = new nextEventsProvider(context);
                        Appointment nextClass = nextEventsProvider.getNextEvent();

                        assertNotNull(nextClass.getTitle());
                        assertNotNull(nextClass.getStartDate());
                    } catch (MalformedURLException | NoConnectionException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }});

        }

    }
