package com.shane.popularmovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.shane.popularmovies.R;
import com.shane.popularmovies.utils.PreferenceUtils;

/**
 * Created by Shane on 8/17/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String EMPTY_STRING = "";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_movies);

        setupPreferenceSummaries();
    }

    private void setupPreferenceSummaries() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);

            if ( ! (preference instanceof CheckBoxPreference)) {
                final String value = sharedPreferences.getString(preference.getKey(), EMPTY_STRING);
                setPreferenceSummary(preference, value);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String sortOrderKey = getString(R.string.pref_sort_by_key);
        final Preference preference = findPreference(key);

        if (null == preference) return;

        if (key.equals(sortOrderKey)) {
            final String value = PreferenceUtils.getSortOrder(getContext());
            setPreferenceSummary(preference, value);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(@NonNull Preference preference, @NonNull Object value) {
        final String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            if (index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);
            }
        } else {
            preference.setSummary(stringValue);
        }
    }
}
