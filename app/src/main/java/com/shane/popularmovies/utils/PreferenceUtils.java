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
        final String popularValue = context.getString(R.string.pref_popular_value);
        return preferences.getString(sortKey, popularValue);
    }

}
