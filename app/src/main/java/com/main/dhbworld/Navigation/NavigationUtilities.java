package com.main.dhbworld.Navigation;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.dhbworld.BlackboardActivity;
import com.main.dhbworld.Calendar.CalendarActivity;
import com.main.dhbworld.Calendar.Events;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NavigationUtilities {
    public static void setUpNavigation(Activity activity, int checkedItem) {
        MaterialToolbar toolbar = activity.findViewById(R.id.topAppBar);

        toolbar.setNavigationOnClickListener(v -> {
            DrawerLayout drawerLayout = activity.findViewById(R.id.drawerLayout);
            drawerLayout.openDrawer(GravityCompat.START);

        });
        NavigationView navigationView = activity.findViewById(R.id.navigationView);
        setupMenu(activity, navigationView, checkedItem);

        navigationView.setNavigationItemSelectedListener(item -> {
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
        });
    }

    private static void setupMenu(Activity activity, NavigationView navigationView, int checkedItem) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String menuOrderJson = sharedPreferences.getString("drawer_menu", "");
        if (menuOrderJson.isEmpty()) {
            navigationView.setCheckedItem(checkedItem);
            return;
        }

        loadEditedMenu(navigationView, checkedItem, menuOrderJson);

    }

    private static void loadEditedMenu(NavigationView navigationView, int checkedItem, String menuOrderJson) {
        Gson gson = new Gson();
        ArrayList<com.main.dhbworld.MenuReorder.MenuItem> menuItems = gson.fromJson(menuOrderJson, new TypeToken<ArrayList<com.main.dhbworld.MenuReorder.MenuItem>>() {}.getType());

        Menu menu = buildMenu(navigationView, menuItems);

        menu.findItem(checkedItem).setCheckable(true);
        menu.findItem(checkedItem).setChecked(true);
    }

    private static Menu buildMenu(NavigationView navigationView, ArrayList<com.main.dhbworld.MenuReorder.MenuItem> menuItems) {
        Menu menu = navigationView.getMenu();
        for (com.main.dhbworld.MenuReorder.MenuItem menuItem : menuItems) {
            int resId = getResId(menuItem.getResourceId(), R.id.class);
            menu.removeItem(resId);
            if (!menuItem.isHidden()) {
                menu.add(R.id.group1, resId, Menu.NONE, menuItem.getTitle());
            }
        }
        MenuItem settings = menu.findItem(R.id.Settings);
        menu.removeItem(R.id.Settings);
        menu.add(settings.getGroupId(), R.id.Settings, settings.getOrder(), settings.getTitle());
        return menu;
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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

    public static void setupLangAndDarkmode(Context context, Resources resources) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int darkmode = Integer.parseInt(defaultSharedPreferences.getString("darkmode", "-1"));
        AppCompatDelegate.setDefaultNightMode(darkmode);

        String language = defaultSharedPreferences.getString("language", "default");
        if (language.equals("default")) {
            Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            Locale.setDefault(locale);
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } else {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }
}
