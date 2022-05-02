package com.main.dhbworld;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest {

    private CalendarActivity calendarActivity;
    SharedPreferences sp;
    String url;

    @Before
    public void createCalendarActivity() {
        // Context of the app under test.
        calendarActivity = new CalendarActivity();
        sp = PreferenceManager.getDefaultSharedPreferences(calendarActivity);
    }

    @Test
    public void getUrl_testing(){
        url = sp.getString("CurrentURL",null);
        assertEquals(calendarActivity.getURL(),url);
    }
}