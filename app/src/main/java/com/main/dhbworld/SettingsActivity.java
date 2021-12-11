package com.main.dhbworld;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsActivity extends AppCompatActivity {

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        context = this;
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference informations = findPreference("informations");
            informations.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //dialog anzeigen
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
                    dialog.setTitle("Informationen über die App");
                    //message in res-> values -> strings -> strings.xml
                    dialog.setMessage(getResources().getString(R.string.informations_message, BuildConfig.VERSION_NAME));
                    dialog.setPositiveButton("Schließen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });

            Preference licenses = findPreference("licenses");
            licenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(context, LicenseActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            SwitchPreference mensaPreference = findPreference("notifications_mensa");
            SwitchPreference coffeePreference = findPreference("notifications_coffee");
            SwitchPreference printerPreference = findPreference("notifications_printer");

            mensaPreference.setOnPreferenceChangeListener(this);
            coffeePreference.setOnPreferenceChangeListener(this);
            printerPreference.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            //switch (preference.getKey()) {
            //    case "notifications_mensa":
            //        if ((boolean) newValue) {
            //            Utilities.subscribeToTopic(Utilities.CATEGORY_CAFETERIA);
            //        } else {
            //            Utilities.unsubscribeFromTopic(Utilities.CATEGORY_CAFETERIA);
            //        }
            //        break;
            //    case "notifications_coffee":
            //        if ((boolean) newValue) {
            //            Utilities.subscribeToTopic(Utilities.CATEGORY_COFFEE);
            //        } else {
            //            Utilities.unsubscribeFromTopic(Utilities.CATEGORY_COFFEE);
            //        }
            //        break;
            //    case "notifications_printer":
            //        if ((boolean) newValue) {
            //            Utilities.subscribeToTopic(Utilities.CATEGORY_PRINTER);
            //        } else {
            //            Utilities.unsubscribeFromTopic(Utilities.CATEGORY_PRINTER);
            //        }
            //        break;
            //}
            return true;
        }
    }
}