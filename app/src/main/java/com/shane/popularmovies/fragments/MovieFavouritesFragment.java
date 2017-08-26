package com.shane.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.shane.popularmovies.models.Movie;

import java.util.List;

import io.reactivex.annotations.NonNull;
import timber.log.Timber;

/**
 * Created by Shane on 8/25/2017.
 */

public class MovieFavouritesFragment extends MovieListFragment {

    public static final String TAG = MovieFavouritesFragment.class.getName();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMoviesFromCache();

    }

    private void loadMoviesFromCache() {
        movieRepository.loadMoviesFromCache()
                .doOnSubscribe(disposable -> {
                    showLoading();
                    movieAdapter.clearMovies();
                })
                .subscribe(
                        this::handleMoviesLoaded,
                        this::handleLoadError,
                        this::handleLoadComplete
                );
    }

    private void showLoading() {
        errorLinearLayout.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void handleLoadComplete() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void handleLoadError(Throwable error) {
        Timber.e(error);
        showErrorMessage("Failed to load movies");
    }

    private void handleMoviesLoaded(@NonNull List<Movie> movies) {
        movieListRecyclerView.setVisibility(View.VISIBLE);
        movieAdapter.setMovies(movies);
    }

    private void showErrorMessage(@NonNull String message) {
        errorMessageTextView.setText(message);
        loadingProgressBar.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.GONE);
        errorLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {

    }
}
