package com.main.dhbworld.Navigation;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.main.dhbworld.BlackboardActivity;
import com.main.dhbworld.Calendar.CalendarActivity;
import com.main.dhbworld.CantineActivity;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.KVVActivity;
import com.main.dhbworld.MainActivity;
import com.main.dhbworld.MapActivity;
import com.main.dhbworld.Organizer.OrganizerActivity;
import com.main.dhbworld.R;
import com.main.dhbworld.SettingsActivity;
import com.main.dhbworld.UserInteractionActivity;

import java.util.List;

public class NavigationUtilities {
    public static void setUpNavigation(Activity activity, int checkedItem) {
        MaterialToolbar toolbar = activity.findViewById(R.id.topAppBar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = activity.findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        NavigationView navigationView = activity.findViewById(R.id.navigationView);
        navigationView.setCheckedItem(checkedItem);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked()) {
                    return false;
                }
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        startActivity(activity,DashboardActivity.class);
                        break;
                    case R.id.personalInformation:
                        startActivity(activity, MainActivity.class);
                        break;
                    case R.id.Calendar:
                        startActivity(activity, CalendarActivity.class);
                        break;
                    case R.id.UserInteraction:
                        startActivity(activity, UserInteractionActivity.class);
                        break;
                    case R.id.Settings:
                        startActivity(activity, SettingsActivity.class);
                        break;
                    case R.id.tram_departure:
                        startActivity(activity, KVVActivity.class);
                        break;
                    case R.id.cantine:
                        startActivity(activity, CantineActivity.class);
                        break;
                    case R.id.dualis:
                        startActivity(activity, DualisActivity.class);
                        break;
                    case R.id.organizer:
                        startActivity(activity, OrganizerActivity.class);
                        break;
                    case R.id.map:
                        startActivity(activity, MapActivity.class);
                        break;
                    case R.id.blackboard:
                        startActivity(activity, BlackboardActivity.class);
                        break;
                }
                return true;
            }
        });
    }

    private static void startActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (!(activity instanceof DashboardActivity)) {
            activity.finish();
        }
        ActivityManager activityManager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo info : tasks) {
            if (info.toString().contains("OrganizerActivity")) {
                Intent dashboardIntent = new Intent(activity, DashboardActivity.class);
                dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(dashboardIntent);
                Intent goalIntent = new Intent(activity, cls);
                goalIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(goalIntent);
            }
        }
    }
}
