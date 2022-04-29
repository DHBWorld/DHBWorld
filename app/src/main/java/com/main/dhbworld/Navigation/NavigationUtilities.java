package com.main.dhbworld.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.main.dhbworld.CalendarActivity;
import com.main.dhbworld.CantineActivity;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.KVVActivity;
import com.main.dhbworld.MainActivity;
import com.main.dhbworld.R;
import com.main.dhbworld.SettingsActivity;
import com.main.dhbworld.UserInteraction;

import java.util.Calendar;

public class NavigationUtilities {
    public static void setUpNavigation(AppCompatActivity activity, int checkedItem) {
        MaterialToolbar toolbar = activity.findViewById(R.id.topAppBar);
        activity.setSupportActionBar(toolbar);
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
                    case R.id.personalInformationNav:
                        startActivity(activity, MainActivity.class);
                        break;
                    case R.id.Calendar:
                        startActivity(activity, CalendarActivity.class);
                        break;
                    case R.id.UserInteraction:
                        startActivity(activity, UserInteraction.class);
                        break;
                    case R.id.Settings:
                        startActivity(activity, SettingsActivity.class);
                        break;
                    case R.id.tram_departure:
                        startActivity(activity, KVVActivity.class);
                        break;
                    case R.id.cantine:
                        startActivity(activity, CantineActivity.class);
                    case R.id.dashboard:
                        startActivity(activity, DashboardActivity.class);
                }

                return true;
            }
        });
    }

    private static void startActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.finish();
    }
}
