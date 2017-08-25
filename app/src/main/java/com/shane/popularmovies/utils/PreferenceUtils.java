package com.shane.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.shane.popularmovies.R;

/**
 * Created by Shane on 8/25/2017.
 */

public final class PreferenceUtils {

    public static String getSortOrder(@NonNull Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String sortKey = context.getString(R.string.pref_sort_by_key);
        final String popularValue = context.getString(R.string.pref_popular_value); // default
        return preferences.getString(sortKey, popularValue);
    }

    public static void setSortOrder(@NonNull Context context, @NonNull String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String key = context.getString(R.string.pref_sort_by_key);
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean getShouldShowFavourites(@NonNull Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String showFavouritesKey = context.getString(R.string.pref_favourite_key);
        final boolean defaultValue = context
                .getResources().getBoolean(R.bool.pref_favourite_default_value);
        return preferences.getBoolean(showFavouritesKey, defaultValue);
    }

    public static void setShouldShowFavourites(@NonNull Context context, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String key = context.getString(R.string.pref_favourite_key);
        editor.putBoolean(key, value);
        editor.apply();
    }
}
