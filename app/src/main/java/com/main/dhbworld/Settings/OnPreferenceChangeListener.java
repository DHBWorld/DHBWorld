package com.main.dhbworld.Settings;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Dualis.service.EverlastingService;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.R;

import java.util.Locale;

public class OnPreferenceChangeListener {

    private final Context context;
    private final Activity activity;

    private final SharedPreferences sharedPrefDualis;

    public OnPreferenceChangeListener(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sharedPrefDualis = context.getSharedPreferences("Dualis", MODE_PRIVATE);
    }

    public boolean darkmode(Object newValue) {
        int darkmode = Integer.parseInt((String) newValue);
        AppCompatDelegate.setDefaultNightMode(darkmode);
        return true;
    }

    public boolean language(Object newValue) {
        Locale locale;
        if (((String) newValue).equals("default")) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);

        } else {
            locale = new Locale((String) newValue);
        }
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        activity.recreate();
        return true;
    }

    public boolean notificationsMensa(Object newValue) {
        if ((boolean) newValue) {
            Utilities.subscribeToTopic(Utilities.CATEGORY_CAFETERIA);
        } else {
            Utilities.unsubscribeFromTopic(Utilities.CATEGORY_CAFETERIA);
        }
        return true;
    }

    public boolean notificationsCoffee(Object newValue) {
        if ((boolean) newValue) {
            Utilities.subscribeToTopic(Utilities.CATEGORY_COFFEE);
        } else {
            Utilities.unsubscribeFromTopic(Utilities.CATEGORY_COFFEE);
        }
        return true;
    }

    public boolean notificationsPrinter(Object newValue) {
        if ((boolean) newValue) {
            Utilities.subscribeToTopic(Utilities.CATEGORY_PRINTER);
        } else {
            Utilities.unsubscribeFromTopic(Utilities.CATEGORY_PRINTER);
        }
        return true;
    }

    public boolean sync(Object newValue) {
        showNoImpactSnackbar();
        if ((boolean) newValue) {
            context.startService(new Intent(context, EverlastingService.class));
        }
        return true;
    }

    public boolean syncTime() {
        showNoImpactSnackbar();
        return true;
    }

    private void showNoImpactSnackbar() {
        if (!sharedPrefDualis.getBoolean("saveCredentials", true)) {
            Snackbar.make(activity.findViewById(android.R.id.content), context.getResources().getString(R.string.sync_makes_no_difference), Snackbar.LENGTH_LONG).show();
        }
    }
}
