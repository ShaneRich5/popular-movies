package com.shane.popularmovies.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shane.popularmovies.GridSpaceItemDecoration;
import com.shane.popularmovies.R;
import com.shane.popularmovies.adapters.MovieAdapter;
import com.shane.popularmovies.listeners.EndlessRecyclerOnScrollListener;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.network.MovieApi;
import com.shane.popularmovies.repositories.MovieApiRepository;
import com.shane.popularmovies.repositories.MovieRepository;

import java.net.UnknownHostException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import timber.log.Timber;


public class MovieListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String POPULAR_SORT_ORDER = "popular";
    public static final String TOP_RATED_SORT_ORDER = "top_rated";

    private MovieAdapter movieAdapter;
    private MovieRepository movieRepository;
    private EndlessRecyclerOnScrollListener scrollListener;

    private String sortOrder;

    @BindView(R.id.movie_list_recycler) RecyclerView movieListRecyclerView;
    @BindView(R.id.load_progress_bar) ProgressBar loadingProgressBar;
    @BindView(R.id.error_message_text_view) TextView errorMessageTextView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    public MovieListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setupSharedPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        readSharedPreferences();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int NUM_OF_GRID_COLUMNS = 2;
        final int PIXEL_GRID_SPACING = 10;

        MovieApi api = MovieApi.Factory.create(getString(R.string.themoviedb_key));
        movieRepository = new MovieApiRepository(api, getContext());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUM_OF_GRID_COLUMNS);
        movieListRecyclerView.addItemDecoration(new GridSpaceItemDecoration(PIXEL_GRID_SPACING));
        movieListRecyclerView.setLayoutManager(gridLayoutManager);
        movieListRecyclerView.setHasFixedSize(true);

        if (!(getActivity() instanceof MovieAdapter.MovieAdapterOnClickHandler)) {
            throw new ClassCastException("Activity must implement MovieAdapterOnClickHandler");
        }

        movieAdapter = new MovieAdapter(getContext(), (MovieAdapter.MovieAdapterOnClickHandler) getActivity());
        movieListRecyclerView.setAdapter(movieAdapter);

        scrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                fetchMovies(currentPage);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(this);

        movieListRecyclerView.addOnScrollListener(scrollListener);
        loadMoviesOnFirstPage();
    }

    private void loadMoviesOnFirstPage() {
        fetchMovies(1);
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

    @Override
    public void onRefresh() {
        resetRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movie_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                SortOrderDialogFragment sortOrderFragment = new SortOrderDialogFragment();
                sortOrderFragment.show(getActivity().getSupportFragmentManager(), "SortOrderFragment");
                return true;
            case R.id.menu_refresh:
                resetRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getResources().getString(R.string.pref_sort_by_key))) {
            String defaultValue = getString(R.string.pref_popular_value);
            String value = sharedPreferences.getString(key, defaultValue);
            if (sortOrder.equals(value)) return;
            sortOrder = value;
            resetRecyclerView();
        }
    }

    private void setupSharedPreferences() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
        readSharedPreferences();
    }

    private void readSharedPreferences() {
        final String key = getString(R.string.pref_sort_by_key);
        final String defaultValue = getString(R.string.pref_popular_value);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sortOrder = sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
