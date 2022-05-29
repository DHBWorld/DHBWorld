package com.main.dhbworld.Cantine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.main.dhbworld.CantineActivity;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Calendar;
import java.util.Date;


@RunWith(AndroidJUnit4.class)
@SmallTest

public class CantineActivityTest {

    @Test
    public void test_currentWeek() throws JSONException {
        Date[] currentWeek= CantineUtilities.generateCurrentWeek();
        assertThat(currentWeek[0].getDay(), is(1));
        assertThat(currentWeek[1].getDay(), is(2));
        assertThat(currentWeek[2].getDay(), is(3));
        assertThat(currentWeek[3].getDay(), is(4));
        assertThat(currentWeek[4].getDay(), is(5));

        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            now.add(Calendar.DATE, 1);
            assertThat(currentWeek[0].getDate(), is(now.getTime().getDate()));
        }
        else if(now.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
            assertThat(currentWeek[0].getDate(), is(now.getTime().getDate()));
        }
        else if(now.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
            now.add(Calendar.DATE, 6);
            assertThat(currentWeek[0].getDate(), is(now.getTime().getDate()));
        }
        else if(now.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
            now.add(Calendar.DATE, 5);
            assertThat(currentWeek[0].getDate(), is(now.getTime().getDate()));
        }
        else if(now.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
            now.add(Calendar.DATE, 4);
            assertThat(currentWeek[0].getDate(), is(now.getTime().getDate()));
        }
        else if(now.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
            now.add(Calendar.DATE, 3);
            assertThat(currentWeek[0].getDate(), is(now.getTime().getDate()));
        }
        else if(now.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
            now.add(Calendar.DATE, 2);
            assertThat(currentWeek[0].getDate(), is(now.getTime().getDate()));
        }

    }

    public void test_loadDataForDashboard(){
        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            assertThat(CantineActivity.loadDataForDashboard().size(), is (0));
        }
        if(now.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
            assertThat(CantineActivity.loadDataForDashboard().size(), is (0));
        }
    }



}
