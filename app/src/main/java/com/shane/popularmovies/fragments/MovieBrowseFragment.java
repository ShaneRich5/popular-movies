package com.shane.popularmovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.shane.popularmovies.R;
import com.shane.popularmovies.listeners.EndlessRecyclerOnScrollListener;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.utils.PreferenceUtils;

import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import timber.log.Timber;

/**
 * Created by Shane on 8/25/2017.
 */

public class MovieBrowseFragment extends MovieListFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private String sortOrder;

    protected EndlessRecyclerOnScrollListener scrollListener;

    public MovieBrowseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setupSharedPreferences();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                fetchMovies(currentPage);
            }
        };

        movieListRecyclerView.addOnScrollListener(scrollListener);
        loadMoviesOnFirstPage();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movie_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                resetRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sortOrder = PreferenceUtils.getSortOrder(getContext());
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getResources().getString(R.string.pref_sort_by_key))) {
            final String sortValue = PreferenceUtils.getSortOrder(getContext());
            if (sortOrder.equals(sortValue)) return;
            sortOrder = sortValue;
            resetRecyclerView();
        }
    }


    @Override
    public void onRefresh() {
        resetRecyclerView();
    }

    private void resetRecyclerView() {
        movieAdapter.clearMovies();
        scrollListener.reset();
        loadMoviesOnFirstPage();
    }

    private void fetchMovies(int page) {
        fetchMoviesBySortOrder(sortOrder, page)
                .doOnSubscribe(disposable -> showLoading())
                .subscribe(
                        movies -> this.handleMoviesLoaded(movies, page),
                        this::handlerLoadingError,
                        this::handleLoadingComplete);
    }

    private void setupSharedPreferences() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
        sortOrder = PreferenceUtils.getSortOrder(getContext());
    }

    private void loadMoviesOnFirstPage() {
        fetchMovies(1);
    }

    private Observable<List<Movie>> fetchMoviesBySortOrder(@NonNull String sortOrder, int page) {
        return movieRepository.fetchMovies(sortOrder, page);
    }

    private void handleLoadingComplete() {
        loadingProgressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void handleMoviesLoaded(@NonNull List<Movie> movies, int page) {
        movieListRecyclerView.setVisibility(View.VISIBLE);
        if (page == 1) movieAdapter.setMovies(movies);
        else movieAdapter.addMovies(movies);
    }

    private void handlerLoadingError(@NonNull Throwable error) {
        if (error instanceof UnknownHostException) {
            showErrorMessage("Please connect to the internet");
        } else {
            showErrorMessage(error.getMessage());
        }
        Timber.e(error);
    }



    private void showErrorMessage(@NonNull String message) {
        errorMessageTextView.setText(message);
        loadingProgressBar.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.GONE);
    }
}
