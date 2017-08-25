package com.shane.popularmovies.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;

import com.shane.popularmovies.R;

/**
 * Created by Shane on 8/25/2017.
 */

public class SortOrderDialogFragment extends DialogFragment {
    public static final String TAG = SortOrderDialogFragment.class.getName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getString(R.string.pref_sort_by_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).setItems(R.array.pref_sort_by_labels,
                (dialogInterface, index) -> updatePreferences(index));
        return builder.create();
    }

    private void updatePreferences(int index) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        String key = getString(R.string.pref_sort_by_key);
        String popular = getString(R.string.pref_popular_value);
        String topRated = getString(R.string.pref_top_rated_value);
        editor.putString(key, (index == 0) ? popular : topRated);
        editor.apply();
    }
}
