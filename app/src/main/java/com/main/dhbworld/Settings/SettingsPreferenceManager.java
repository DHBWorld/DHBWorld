package com.main.dhbworld.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsPreferenceManager {

    private final PreferenceFragmentCompat context;

    public SettingsPreferenceManager(PreferenceFragmentCompat context) {
        this.context = context;
    }

    public void addPreference(String key, Preference.OnPreferenceClickListener preferenceClickListener) {
        Preference preference = context.findPreference(key);
        if (preference != null) preference.setOnPreferenceClickListener(preferenceClickListener);
    }

    public void addPreference(String key, Preference.OnPreferenceChangeListener preferenceChangeListener) {
        Preference preference = context.findPreference(key);
        if (preference != null) preference.setOnPreferenceChangeListener(preferenceChangeListener);
    }

    public <T extends Preference> T addPreference(String key) {
        return context.findPreference(key);
    }
}
