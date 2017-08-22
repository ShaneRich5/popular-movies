package com.shane.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Shane on 8/17/2017.
 */

public final class MovieContract {

    private MovieContract() {
        throw new AssertionError("Cannot instantiate MovieContract");
    }

    public static final String CONTENT_AUTHORITY = "com.shane.popularmovies.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RATINGS = "ratings";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildMovieUriWithId(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }
    }
}
