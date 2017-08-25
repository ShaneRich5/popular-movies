package com.shane.popularmovies.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
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
    public Observable<List<Movie>> fetchMovies(@NonNull String sortOrder, int page) {
        return movieApi.getMovies(sortOrder, page)
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
        context.getContentResolver().delete(MovieEntry.buildMovieUriWithId(movie.getId()), null, null);
    }

    @Override
    public void saveFavourite(@NonNull Movie movie) {
        final Uri queryUri = MovieEntry.buildMovieUriWithId(movie.getId());
        final Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null);

        if (null == cursor) {
            Timber.d("saveFavourite: Empty cursor");
            return;
        }

        final ContentValues values = new Movie.Builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .ratings(movie.getRatings())
                .synopsis(movie.getSynopsis())
                .posterPath(movie.getPosterPath())
                .releaseDate(movie.getReleaseDate())
                .build();

        if (cursor.getCount() == 0) {
            context.getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        }

        cursor.close();
    }

    @Override
    public Observable<List<Movie>> loadMoviesFromCache() {
        return readMoviesFromDatabase()
                .map(CURSOR_MOVIE_LIST_MAPPER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Cursor> readMoviesFromDatabase() {
        return Observable.create(observer -> {
            Cursor cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);
            observer.onNext(cursor);
        });
    }

    private static Function<Cursor, List<Movie>> CURSOR_MOVIE_LIST_MAPPER = cursor -> {
        List<Movie> movies = new ArrayList<>();
        while (cursor.moveToNext()) {
            final int id = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
            final String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
            final String posterPath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
            final String synopsis = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_SYNOPSIS));
            final double ratings = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_RATINGS));

            final String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));

            final Movie movie = new Movie(id, title, posterPath, synopsis, ratings, releaseDate);
            movies.add(movie);
        }
        return movies;
    };
}
