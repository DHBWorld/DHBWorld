package com.main.dhbworld;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.provider.Settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.work.WorkManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Dualis.DualisAPI;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Navigation.NavigationUtilities;

public class SettingsActivity extends AppCompatActivity {

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        NavigationUtilities.setUpNavigation(this, R.id.Settings);

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
            assert informations != null;
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
            assert licenses != null;
            licenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(context, LicenseActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference privacy = findPreference("dataprivacy");
            assert privacy != null;
            privacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(context, DataPrivacyActivity.class);
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


            Preference calendar = findPreference("calendarURL");
            calendar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle("Please enter your Rapla-URL");
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    final EditText urlInput = new EditText(context);
                    urlInput.setHint("Type URL here");
                    urlInput.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                    builder.setView(urlInput);
                    builder.setCancelable(true);

                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String urlString = urlInput.getText().toString();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("CurrentURL", urlString);
                            editor.apply();
                        }
                    });
                    builder.create();

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });


            SwitchPreference useBiometricsPreference = findPreference("useBiometrics");
            SwitchPreference notificationPreference = findPreference("sync");
            ListPreference notificationTimePreference = findPreference("sync_time");
            Preference customizeNotificationPreference = findPreference("customize_notification");


            BiometricManager biometricManager = BiometricManager.from(getContext());
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_SUCCESS){
                useBiometricsPreference.setEnabled(false);
            }
            notificationPreference.setOnPreferenceChangeListener(this);
            notificationTimePreference.setOnPreferenceChangeListener(this);
            customizeNotificationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());

                    startActivity(settingsIntent);
                    return true;
                }
            });
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SharedPreferences sharedPref = getContext().getSharedPreferences("Dualis", MODE_PRIVATE);
            switch (preference.getKey()) {
                case "notifications_mensa":
                    if ((boolean) newValue) {
                        Utilities.subscribeToTopic(Utilities.CATEGORY_CAFETERIA);
                    } else {
                        Utilities.unsubscribeFromTopic(Utilities.CATEGORY_CAFETERIA);
                    }
                    break;
                case "notifications_coffee":
                    if ((boolean) newValue) {
                        Utilities.subscribeToTopic(Utilities.CATEGORY_COFFEE);
                    } else {
                        Utilities.unsubscribeFromTopic(Utilities.CATEGORY_COFFEE);
                    }
                    break;
                case "notifications_printer":
                    if ((boolean) newValue) {
                        Utilities.subscribeToTopic(Utilities.CATEGORY_PRINTER);
                    } else {
                        Utilities.unsubscribeFromTopic(Utilities.CATEGORY_PRINTER);
                    }
                    break;
                case "sync":
                    if (!sharedPref.getBoolean("saveCredentials", true)) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), getContext().getResources().getString(R.string.sync_makes_no_difference), Snackbar.LENGTH_LONG).show();
                    }
                    if (!(boolean) newValue) {
                        WorkManager.getInstance(getContext()).cancelUniqueWork("DualisNotifier");
                    } else {
                        DualisAPI.setAlarmManager(getContext());
                    }
                    return true;
                case "sync_time":
                    if (!sharedPref.getBoolean("saveCredentials", true)) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), getContext().getResources().getString(R.string.sync_makes_no_difference), Snackbar.LENGTH_LONG).show();
                    }
                    DualisAPI.setAlarmManager(getContext());
                    return true;
            }
            return true;
        }
    }
}