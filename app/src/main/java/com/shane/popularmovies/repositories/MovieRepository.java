package com.shane.popularmovies.repositories;

import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.Review;
import com.shane.popularmovies.models.Trailer;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Shane on 8/5/2017.
 */

public interface MovieRepository {

    Observable<List<Movie>> fetchTopRatedMovies(int page);

    Observable<List<Movie>> fetchPopularMovies(int page);

    Observable<List<Review>> fetchMovieReviews(int movieId);

    Observable<List<Trailer>> fetchMovieTrailers(int movieId);
}
