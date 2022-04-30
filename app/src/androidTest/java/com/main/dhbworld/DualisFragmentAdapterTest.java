package com.main.dhbworld;

import static org.junit.Assert.assertTrue;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.main.dhbworld.Dualis.DualisFragmentAdapter;
import com.main.dhbworld.Dualis.DualisOverallFragment;
import com.main.dhbworld.Dualis.DualisSemesterFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DualisFragmentAdapterTest {
    @Test
    public void returnDualisOverallFragment() {

        ActivityScenario<DualisActivity> activityScenario = ActivityScenario.launch(DualisActivity.class);

        activityScenario.onActivity(activity -> {
            DualisFragmentAdapter fragmentAdapter = new DualisFragmentAdapter(activity, null, null);
            Fragment fragment = fragmentAdapter.createFragment(0);

            assertTrue(fragment instanceof DualisOverallFragment);
        });
    }

    @Test
    public void returnDualisSemesterFragment() {

        ActivityScenario<DualisActivity> activityScenario = ActivityScenario.launch(DualisActivity.class);

        activityScenario.onActivity(activity -> {
            DualisFragmentAdapter fragmentAdapter = new DualisFragmentAdapter(activity, null, null);
            Fragment fragment = fragmentAdapter.createFragment(1);

            assertTrue(fragment instanceof DualisSemesterFragment);
        });
    }
}
