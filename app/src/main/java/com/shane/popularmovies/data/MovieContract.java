package com.shane.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by Shane on 8/17/2017.
 */

public class MovieContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";

    }
}
