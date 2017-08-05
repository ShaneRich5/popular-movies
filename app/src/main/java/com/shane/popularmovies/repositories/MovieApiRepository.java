package com.shane.popularmovies.repositories;

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
        return movieApi.listTopRatedMovies(page)
                .map(MovieResponse::getMovies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Movie>> fetchPopularMovies(int page) {
        return movieApi.listPopularMovies(page)
                .map(MovieResponse::getMovies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
