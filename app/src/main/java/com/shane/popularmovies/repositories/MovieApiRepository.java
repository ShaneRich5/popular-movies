package com.shane.popularmovies.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.shane.popularmovies.data.MovieContract.MovieEntry;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.MovieResponse;
import com.shane.popularmovies.models.Review;
import com.shane.popularmovies.models.ReviewResponse;
import com.shane.popularmovies.models.Trailer;
import com.shane.popularmovies.models.TrailerResponse;
import com.shane.popularmovies.network.MovieApi;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Shane on 8/5/2017.
 */

public class MovieApiRepository implements MovieRepository {

    private final MovieApi movieApi;
    private final Context context;

    public MovieApiRepository(MovieApi movieApi, Context context) {
        this.movieApi = movieApi;
        this.context = context;
    }

    @Override
    public Observable<List<Movie>> fetchTopRatedMovies(int page) {
        return movieApi.getTopRatedMovies(page)
                .map(MovieResponse::getMovies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Movie>> fetchPopularMovies(int page) {
        return movieApi.getPopularMovies(page)
                .map(MovieResponse::getMovies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Review>> fetchMovieReviews(int movieId) {
        return movieApi.getReviews(movieId)
                .map(ReviewResponse::getReviews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Trailer>> fetchMovieTrailers(int movieId) {
        return movieApi.getTrailers(movieId)
                .map(TrailerResponse::getTrailers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void removeFavourite(@NonNull Movie movie) {
        Timber.i("removeFavourite called");
        context.getContentResolver().delete(MovieEntry.buildMovieUriWithId(movie.getId()), null, null);
    }

    @Override
    public void saveFavourite(@NonNull Movie movie) {
        Timber.d("saveFavourite called");
        final Uri queryUri = MovieEntry.buildMovieUriWithId(movie.getId());
        final Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null);

        if (null == cursor) {
            Timber.d("saveFavourite: Empty cursor");
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieEntry.COLUMN_RATINGS, movie.getRatings());
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());

        Timber.d("All Cursor: %s", DatabaseUtils.dumpCursorToString(cursor));

        if (cursor.getCount() == 0) {
            context.getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);
        }
        cursor.close();
    }
}
