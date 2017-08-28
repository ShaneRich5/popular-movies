package com.shane.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.shane.popularmovies.data.MovieContract.MovieEntry;

import timber.log.Timber;

/**
 * Created by Shane on 8/22/2017.
 */

public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieDbHelper movieDbHelper;

    static {
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_MOVIES, CODE_MOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArguments, @Nullable String sortOrder) {
        final SQLiteDatabase database = movieDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection,
                        selectionArguments, null, null, sortOrder);
                break;
            case CODE_MOVIE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                cursor = database.query(MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                long id = 0;
                if (contentValues != null) {
                    id = database.insert(MovieEntry.TABLE_NAME, null, contentValues);
                }
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    final SQLException exception =  new SQLException("Failed to insert row");
                    Timber.e(exception);
                    throw exception;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        notifyChange(uri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] arguments) {
        final SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        int numberOfRowsDeleted;

        if (null == selection) selection = "1";

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                numberOfRowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, arguments);
                break;
            case CODE_MOVIE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                numberOfRowsDeleted = database.delete(MovieEntry.TABLE_NAME,
                        MovieEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numberOfRowsDeleted != 0) notifyChange(uri);
        return numberOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] arguments) {
        final SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        int numberOfRowsUpdated;


        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                numberOfRowsUpdated = database.update(MovieEntry.TABLE_NAME, contentValues, selection, arguments);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numberOfRowsUpdated != 0) {
            notifyChange(uri);
        }

        return numberOfRowsUpdated;
    }

    private void notifyChange(@NonNull Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
