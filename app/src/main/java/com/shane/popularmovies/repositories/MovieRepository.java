package com.shane.popularmovies.repositories;

import android.support.annotation.NonNull;

import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.Review;
import com.shane.popularmovies.models.Trailer;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Shane on 8/5/2017.
 */

public interface MovieRepository {

    Observable<List<Movie>> fetchMovies(@NonNull String sortOrder, int page);

    Observable<List<Review>> fetchMovieReviews(int movieId);

    Observable<List<Trailer>> fetchMovieTrailers(int movieId);

    void removeFavourite(@NonNull Movie movie);

    void saveFavourite(@NonNull Movie movie);

    Observable<List<Movie>> loadMoviesFromCache();

    Observable<Movie> loadMovieFromCache(int id);
}
