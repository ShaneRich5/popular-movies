package com.shane.popularmovies.utils;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Shane on 8/25/2017.
 */

public final class DateUtils {

    public static String formatDate(@NonNull String rawDate) {
        try {
            final SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-M-dd", Locale.US);
            final SimpleDateFormat targetFormat = new SimpleDateFormat("M dd, yyyy", Locale.US);
            final Date date = originalFormat.parse(rawDate);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return rawDate;
        }
    }
}
