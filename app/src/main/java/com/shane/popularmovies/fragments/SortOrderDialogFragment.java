package com.shane.popularmovies.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.shane.popularmovies.R;

import timber.log.Timber;

/**
 * Created by Shane on 8/25/2017.
 */

public class SortOrderDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final String title = getString(R.string.pref_sort_by_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setItems(R.array.pref_sort_by_labels, (dialogInterface, index) -> {
                    Timber.i("Selected" + index);

                });
        return builder.create();
    }
}
