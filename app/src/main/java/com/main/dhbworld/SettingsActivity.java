package com.main.dhbworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.Settings.OnPreferenceChangeListener;
import com.main.dhbworld.Settings.OnPreferenceClickListener;
import com.main.dhbworld.Settings.SettingsPreferenceManager;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        NavigationUtilities.setUpNavigation(this, R.id.Settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        Context context;
        AppCompatActivity activity;

        OnPreferenceChangeListener changeListener;
        OnPreferenceClickListener clickListener;

        ActivityResultLauncher<Intent> activityResultLauncherExportDebugLog;
        ActivityResultLauncher<Intent> activityResultLauncherExportBackup;
        ActivityResultLauncher<Intent> activityResultLauncherRestoreBackup;
        ActivityResultLauncher<Intent> activityResultLauncherReorder;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activityResultLauncherExportDebugLog = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> clickListener.handleDebugLogResult(result.getData()));

            activityResultLauncherExportBackup = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> clickListener.handleExportBackup(result.getData()));

            activityResultLauncherRestoreBackup = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> clickListener.handleRestoreBackup(result.getData()));

            activityResultLauncherReorder = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                            result -> NavigationUtilities.setUpNavigation(activity, R.id.Settings));

        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            context = getContext();
            activity = (AppCompatActivity) getActivity();
            if (context == null || activity == null) {
                return;
            }

            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SettingsPreferenceManager preferenceManager = new SettingsPreferenceManager(this);

            changeListener = new OnPreferenceChangeListener(context, activity);
            clickListener = new OnPreferenceClickListener(context, activity);

            initializePreferences(preferenceManager);
            initializeBiometricsPreference(preferenceManager);
        }

        private void initializePreferences(SettingsPreferenceManager preferenceManager) {
            preferenceManager.addPreference("darkmode", (preference, newValue) -> changeListener.darkmode(newValue));
            preferenceManager.addPreference("language", (preference, newValue) -> changeListener.language(newValue));
            preferenceManager.addPreference("reorder_menu", preference -> clickListener.reorderMenu(activityResultLauncherReorder));
            preferenceManager.addPreference("informations", preference -> clickListener.information());
            preferenceManager.addPreference("licenses", preference -> clickListener.licenses());
            preferenceManager.addPreference("dataprivacy", preference -> clickListener.dataPrivacy());
            preferenceManager.addPreference("feedback", preference -> clickListener.feedback());
            preferenceManager.addPreference("notifications_mensa", (preference, newValue) -> changeListener.notificationsMensa(newValue));
            preferenceManager.addPreference("notifications_coffee", (preference, newValue) -> changeListener.notificationsCoffee(newValue));
            preferenceManager.addPreference("notifications_printer", (preference, newValue) -> changeListener.notificationsPrinter(newValue));
            preferenceManager.addPreference("sync", (preference, newValue) -> changeListener.sync(newValue));
            preferenceManager.addPreference("sync_time", (preference, newValue) -> changeListener.syncTime());
            preferenceManager.addPreference("customize_notification", preference -> clickListener.customizeNotification());
            preferenceManager.addPreference("calendarURL", preference -> clickListener.calenderURL());
            preferenceManager.addPreference("exportDebugLog", preference -> clickListener.exportDebugLog(activityResultLauncherExportDebugLog));
            preferenceManager.addPreference("exportBackup", preference -> clickListener.exportBackup(activityResultLauncherExportBackup));
            preferenceManager.addPreference("restoreBackup", preference -> clickListener.restoreBackup(activityResultLauncherRestoreBackup));
        }

        private void initializeBiometricsPreference(SettingsPreferenceManager preferenceManager) {
            SwitchPreference useBiometricsPreference = preferenceManager.addPreference("useBiometrics");
            BiometricManager biometricManager = BiometricManager.from(context);
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_SUCCESS){
                Objects.requireNonNull(useBiometricsPreference).setEnabled(false);
            }
        }
    }
}