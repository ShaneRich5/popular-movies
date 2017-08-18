package com.shane.popularmovies.repositories;

import com.shane.popularmovies.models.Review;
import com.shane.popularmovies.models.ReviewResponse;
import com.shane.popularmovies.models.Trailer;
import com.shane.popularmovies.models.TrailerResponse;
import com.shane.popularmovies.network.MovieApi;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.MovieResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Shane on 8/5/2017.
 */

public class MovieApiRepository implements MovieRepository {

    private final MovieApi movieApi;

    public MovieApiRepository(MovieApi movieApi) {
        this.movieApi = movieApi;
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
}
