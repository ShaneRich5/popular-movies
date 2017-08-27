package com.shane.popularmovies.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shane.popularmovies.GridSpaceItemDecoration;
import com.shane.popularmovies.R;
import com.shane.popularmovies.adapters.MovieAdapter;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.network.MovieApi;
import com.shane.popularmovies.repositories.MovieApiRepository;
import com.shane.popularmovies.repositories.MovieRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;


public abstract class MovieListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = MovieListFragment.class.getName();
    public static final String MOVIE_GRID_STATE = "movie_grid_state";
    public static final String MOVIE_GRID_ITEMS = "movie_grid_items";

    protected MovieAdapter movieAdapter;
    protected MovieRepository movieRepository;
    protected GridLayoutManager movieLayoutManager;

    @BindView(R.id.movie_list_recycler) RecyclerView movieRecyclerView;
    @BindView(R.id.load_progress_bar) ProgressBar loadingProgressBar;
    @BindView(R.id.error_message_text_view) TextView errorMessageTextView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    public MovieListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, view);


        final int NUM_OF_GRID_COLUMNS = getResources().getInteger(R.integer.movie_grid_column_count);
        final int PIXEL_GRID_SPACING = 10;

        MovieApi api = MovieApi.Factory.create(getString(R.string.themoviedb_key));
        movieRepository = new MovieApiRepository(api, getContext());

        movieLayoutManager = new GridLayoutManager(getContext(), NUM_OF_GRID_COLUMNS);

        movieRecyclerView.addItemDecoration(new GridSpaceItemDecoration(PIXEL_GRID_SPACING));
        movieRecyclerView.setLayoutManager(movieLayoutManager);
        movieRecyclerView.setHasFixedSize(true);

        if (!(getActivity() instanceof MovieAdapter.MovieAdapterOnClickHandler)) {
            throw new ClassCastException("Activity must implement MovieAdapterOnClickHandler");
        }

        movieAdapter = new MovieAdapter(getContext(), (MovieAdapter.MovieAdapterOnClickHandler) getActivity());
        movieRecyclerView.setAdapter(movieAdapter);

        if (savedInstanceState != null) restoreState(savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) return;
        saveState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) restoreState(savedInstanceState);
    }

    private void saveState(@NonNull Bundle outState) {
        final Parcelable gridState = movieRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(MOVIE_GRID_STATE, gridState);
        outState.putParcelableArrayList(MOVIE_GRID_ITEMS, new ArrayList<Parcelable>(movieAdapter.getMovies()));
    }

    private void restoreState(@NonNull Bundle savedInstanceState) {
        final Parcelable gridState = savedInstanceState.getParcelable(MOVIE_GRID_STATE);
        final List<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIE_GRID_ITEMS);
        if (movies != null) handleMoviesLoaded(movies);
        movieRecyclerView.getLayoutManager().onRestoreInstanceState(gridState);
    }

    public abstract void handleMoviesLoaded(List<Movie> movies);

    public abstract void handlerLoadingError(@NonNull Throwable error);

    protected void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.GONE);
    }

    protected void handleLoadingComplete() {
        loadingProgressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
}
