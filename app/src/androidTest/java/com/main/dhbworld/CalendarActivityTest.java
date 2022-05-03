package com.main.dhbworld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alamkanak.weekview.WeekViewEntity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest{

    String url;
    String key;
    ArrayList<String> expectedBlackList;
    SharedPreferences sp;
    Events bottomSheetEvent;
    Calendar cal;


    @Test
    public void getURlTest() {
        try(ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            scenario.onActivity(activity -> {
                sp = PreferenceManager.getDefaultSharedPreferences(activity);
                url = sp.getString("currentURL",null);
                assertEquals(activity.getURL(),url);
            });
        }
    }

    @Test
    public void blackListTest() {
        try(ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            scenario.onActivity(activity -> {
                sp = PreferenceManager.getDefaultSharedPreferences(activity);
                key = "blackList";
                expectedBlackList = new ArrayList<>();
                expectedBlackList.add("Element 1");
                expectedBlackList.add("Element 2");
                activity.saveBlackList(expectedBlackList,key);

                ArrayList<String> realBlackList = CalendarActivity.getBlackList();
                assertEquals(expectedBlackList,realBlackList);
            });
        }
    }

    @Test
    public void bottomSheetTest() {
        try(ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            scenario.onActivity(activity -> {
                Calendar now = Calendar.getInstance();
                Calendar nextWeek = (Calendar) now.clone();
                nextWeek.add(Calendar.WEEK_OF_YEAR,1);
                WeekViewEntity.Style style = new WeekViewEntity.Style.Builder().setBackgroundColor(Color.parseColor("#86c5da")).build();
                bottomSheetEvent = new Events(1,"TestTitle",now,nextWeek,"TestDescription", style);
                activity.showBottomSheet(bottomSheetEvent);
                BottomSheetDialog bottomSheetDialog = activity.bottomSheetDialog;

                assertTrue(bottomSheetDialog.isShowing());
            });
        }
    }

    public void dateTest(){
        try(ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            scenario.onActivity(activity -> {
                //set date to a sunday and call setDates() to see if it switches to next monday.
                cal.set(2022,5,15,12,0,0);
                activity.date = cal;
                activity.setDates();

                assertEquals(activity.date.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY);
            });
        }
    }



}