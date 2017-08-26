package com.shane.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.shane.popularmovies.models.Movie;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;


/**
 * Created by Shane on 8/25/2017.
 */

public class MovieFavouritesFragment extends MovieListFragment {

    public static final String TAG = MovieFavouritesFragment.class.getName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMoviesFromCache();
    }

    private void loadMoviesFromCache() {
        compositeDisposable.add(movieRepository.loadMoviesFromCache()
                .doOnSubscribe(disposable -> {
                    showLoading();
                    movieAdapter.clearMovies();
                })
                .subscribe(
                        this::handleMoviesLoaded,
                        this::handleLoadError,
                        this::handleLoadComplete
                ));
    }

    private void showLoading() {
        errorMessageTextView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void handleLoadComplete() {
        loadingProgressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void handleLoadError(Throwable error) {
        Timber.e(error);
        showErrorMessage("Failed to load movies");
    }

    private void handleMoviesLoaded(@NonNull List<Movie> movies) {
        loadingProgressBar.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        movieAdapter.setMovies(movies);
    }

    private void showErrorMessage(@NonNull String message) {
        errorMessageTextView.setText(message);
        loadingProgressBar.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        loadMoviesFromCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
