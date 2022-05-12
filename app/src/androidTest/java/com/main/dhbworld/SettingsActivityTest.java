package com.main.dhbworld;

import static org.junit.Assert.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    @Test
    public void PreferenceInitTest() {
        ActivityScenario<SettingsActivity> activityScenario = ActivityScenario.launch(SettingsActivity.class);

        activityScenario.onActivity(activity -> assertNotNull(activity.getMainLooper()));
    }
}
