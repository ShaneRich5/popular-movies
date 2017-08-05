package com.shane.popularmovies;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Shane on 8/5/2017.
 */

public interface MovieRepository {

    Observable<List<Movie>> fetchTopRatedMovies(int page);

    Observable<List<Movie>> fetchPopularMovies(int page);
}
