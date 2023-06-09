package com.main.dhbworld.Dashboard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.test.InstrumentationRegistry;
import com.main.dhbworld.Calendar.nextEventsProvider;
import com.main.dhbworld.CantineActivity;
import com.main.dhbworld.Utilities.NetworkAvailability;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import dhbw.timetable.rapla.data.event.Appointment;

public class DashboardTest {

    @Test
    public void saveVisibilityOfCards(){
        Context context = InstrumentationRegistry.getContext();
        SharedPreferences sp = context.getSharedPreferences(Dashboard.dashboardSettings,Context.MODE_PRIVATE);
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
    public void loadCalendarData() {
        Context context = InstrumentationRegistry.getContext();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = preferences.getString("CurrentURL",null);

        if (!(url ==null) && (!url.equals(""))) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nextEventsProvider nextEventsProvider = new nextEventsProvider(context);
                        Appointment nextClass = nextEventsProvider.getNextEvent();

                        assertNotNull(nextClass.getTitle());
                        assertNotNull(nextClass.getStartDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }});}

        }

        @Test
        public void networkAvalabilityIsNotNull(){
            Context appContext = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
            assertNotNull(NetworkAvailability.check(appContext));
        }

        @Test
        public void textNetworkAvalability(){
            Context appContext = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
            if (!NetworkAvailability.check(appContext)){
                MatcherAssert.assertThat(CantineActivity.loadDataForDashboard().size(), is(0));
            }
            if (CantineActivity.loadDataForDashboard().size()>0){
                assertThat(NetworkAvailability.check(appContext), is(true));
            }
        }

    }
