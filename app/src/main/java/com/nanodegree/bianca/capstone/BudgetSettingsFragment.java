package com.nanodegree.bianca.capstone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

public class BudgetSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "BudgetSettingsFragment";

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            /* For list preferences, look up the correct display value in */
            /* the preference's 'entries' list (since they have separate labels/values). */
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            if (preference instanceof EditTextPreference) { // Total budget
                try {
                    Float.valueOf(stringValue);
                } catch (NumberFormatException nfe) {
                    Log.d(TAG, "bib setPreferenceSummary: " + stringValue);
                    stringValue = "1000.00";
                }
            }
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_main, rootKey);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        /* Register the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof SwitchPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, sharedPreferences.getString(key, value));
                sharedPreferences.edit().putString(key, value);
                sharedPreferences.edit().commit();
            }
        }
    }
}
