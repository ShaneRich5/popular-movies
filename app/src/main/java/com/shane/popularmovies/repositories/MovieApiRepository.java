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
}
