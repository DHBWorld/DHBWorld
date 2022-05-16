package com.main.dhbworld;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Debugging.Debugging;
import com.main.dhbworld.Dualis.DualisAPI;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        NavigationUtilities.setUpNavigation(this, R.id.Settings);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(data.getData());
                Files.copy(Debugging.getLog(this).toPath(), outputStream);
                Snackbar.make(this.findViewById(android.R.id.content), this.getString(R.string.file_saved), BaseTransientBottomBar.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(this.findViewById(android.R.id.content), this.getString(R.string.default_error_msg), BaseTransientBottomBar.LENGTH_SHORT).show();
            }

        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        Context context;
        Activity activity;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            context = getContext();
            activity = getActivity();
            if (context == null) {
                return;
            }
            if (activity == null) {
                return;
            }

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference information = findPreference("informations");
            Preference licenses = findPreference("licenses");
            Preference privacy = findPreference("dataprivacy");

            SwitchPreference mensaPreference = findPreference("notifications_mensa");
            SwitchPreference coffeePreference = findPreference("notifications_coffee");
            SwitchPreference printerPreference = findPreference("notifications_printer");

            SwitchPreference useBiometricsPreference = findPreference("useBiometrics");
            SwitchPreference notificationPreference = findPreference("sync");
            ListPreference notificationTimePreference = findPreference("sync_time");
            Preference customizeNotificationPreference = findPreference("customize_notification");

            Preference calendar = findPreference("calendarURL");

            Preference debugLog = findPreference("exportDebugLog");

            Objects.requireNonNull(information).setOnPreferenceClickListener(this);
            Objects.requireNonNull(licenses).setOnPreferenceClickListener(this);
            Objects.requireNonNull(privacy).setOnPreferenceClickListener(this);

            Objects.requireNonNull(mensaPreference).setOnPreferenceChangeListener(this);
            Objects.requireNonNull(coffeePreference).setOnPreferenceChangeListener(this);
            Objects.requireNonNull(printerPreference).setOnPreferenceChangeListener(this);

            Objects.requireNonNull(notificationPreference).setOnPreferenceChangeListener(this);
            Objects.requireNonNull(notificationTimePreference).setOnPreferenceChangeListener(this);
            Objects.requireNonNull(customizeNotificationPreference).setOnPreferenceClickListener(this);
            BiometricManager biometricManager = BiometricManager.from(context);
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_SUCCESS){
                Objects.requireNonNull(useBiometricsPreference).setEnabled(false);
            }

            Objects.requireNonNull(calendar).setOnPreferenceClickListener(this);

            Objects.requireNonNull(debugLog).setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SharedPreferences sharedPref = context.getSharedPreferences("Dualis", MODE_PRIVATE);
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
                        Snackbar.make(activity.findViewById(android.R.id.content), context.getResources().getString(R.string.sync_makes_no_difference), Snackbar.LENGTH_LONG).show();
                    }
                    if (!(boolean) newValue) {
                        WorkManager.getInstance(context).cancelUniqueWork("DualisNotifier");
                    } else {
                        DualisAPI.setAlarmManager(getContext());
                    }
                    return true;
                case "sync_time":
                    if (!sharedPref.getBoolean("saveCredentials", true)) {
                        Snackbar.make(activity.findViewById(android.R.id.content), context.getResources().getString(R.string.sync_makes_no_difference), Snackbar.LENGTH_LONG).show();
                    }
                    DualisAPI.setAlarmManager(getContext());
                    return true;
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "informations":
                    //dialog anzeigen
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
                    dialog.setTitle(R.string.information_about_app);
                    //message in res-> values -> strings -> strings.xml
                    dialog.setMessage(getResources().getString(R.string.informations_message, BuildConfig.VERSION_NAME));
                    dialog.setPositiveButton(getResources().getString(R.string.close), (dialog1, which) -> dialog1.dismiss());
                    dialog.show();
                    return true;
                case "licenses":
                    Intent licenseIntent = new Intent(context, LicenseActivity.class);
                    startActivity(licenseIntent);
                    return true;
                case "dataprivacy":
                    Intent DataPrivacyIntent = new Intent(context, DataPrivacyActivity.class);
                    startActivity(DataPrivacyIntent);
                    return true;
                case "calendarURL":
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    LayoutInflater inflater = getLayoutInflater();
                    View tempView = inflater.inflate(R.layout.urlalertdialog,null);
                    builder.setView(tempView);
                    builder.setTitle("Please enter your Rapla-URL");
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    builder.setCancelable(false);
                    EditText urlEditText = tempView.findViewById(R.id.urlEditText);
                    EditText courseEditText = tempView.findViewById(R.id.urlCourseName);
                    EditText courseDirEditText = tempView.findViewById(R.id.urlCourseDirector);
                    String courseDirector = courseDirEditText.getText().toString();
                    String courseName = courseEditText.getText().toString();
                    String urlString = urlEditText.getText().toString();
                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!courseName.isEmpty() && !urlString.isEmpty()){
                                String urlString = urlEditText.getText().toString();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("CurrentURL", urlString);
                                editor.apply();
                            }
                            else if(urlString.isEmpty() && !courseName.isEmpty()){
                                String formedURL = ("https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=" + courseDirector.toLowerCase() + "&file=" + courseName.toLowerCase());
                                System.out.println(formedURL);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("CurrentURL", formedURL);
                                editor.apply();
                            }
                            else if(!urlString.isEmpty()) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("CurrentURL", urlString);
                                editor.apply();
                            }
                        }
                    });
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create();
                    AlertDialog urlDialog = builder.create();
                    urlDialog.show();
                    return true;
                case "customize_notification":
                    Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());

                    startActivity(settingsIntent);
                    return true;
                case "exportDebugLog":
                    Debugging.createFile(activity, context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toURI());
                    return true;
            }
            return false;
        }
    }
}