package com.shane.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.shane.popularmovies.R;
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

    public static MovieFavouritesFragment newInstance() {
        return new MovieFavouritesFragment();
    }

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
                        this::handlerLoadingError,
                        this::handleLoadComplete
                ));
    }


    private void handleLoadComplete() {
        loadingProgressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void handleMoviesLoaded(@NonNull List<Movie> movies) {
        loadingProgressBar.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
        movieRecyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        movieAdapter.setMovies(movies);
    }

    @Override
    public void handlerLoadingError(@NonNull Throwable error) {
        Timber.e(error);
        showErrorMessage(getString(R.string.error_loading_message));
    }

    private void showErrorMessage(@NonNull String message) {
        errorMessageTextView.setText(message);
        loadingProgressBar.setVisibility(View.GONE);
        movieRecyclerView.setVisibility(View.GONE);
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
