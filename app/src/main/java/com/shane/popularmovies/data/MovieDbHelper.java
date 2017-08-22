package com.shane.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.shane.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Shane on 8/20/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    private static final String SQL_CREATE_MOVIES = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL," +
            MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
            MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
            MovieEntry.COLUMN_RATINGS + " REAL NOT NULL," +
            MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL)";

    private static final String SQL_DELETE_MOVIES = "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public MovieDbHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_MOVIES);
        onCreate(sqLiteDatabase);
    }
}