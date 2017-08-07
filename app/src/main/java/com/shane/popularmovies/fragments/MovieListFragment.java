package com.shane.popularmovies.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import io.reactivex.annotations.NonNull;


public class MovieListFragment extends Fragment {
    private String TAG = MovieListFragment.class.getSimpleName();

    private MovieAdapter movieAdapter;
    private MovieRepository movieRepository;

    @BindView(R.id.movie_list_recycler) RecyclerView movieListRecyclerView;
    @BindView(R.id.load_progress_bar) ProgressBar loadingProgressBar;
    @BindView(R.id.error_message_text_view) TextView errorMessageTextView;
    @BindView(R.id.error_layout) ConstraintLayout errorLayout;

    public MovieListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MovieApi api = MovieApi.Factory.create(getString(R.string.themoviedb_key));
        movieRepository = new MovieApiRepository(api);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        movieListRecyclerView.setLayoutManager(gridLayoutManager);
        movieListRecyclerView.setHasFixedSize(true);

        if (!(getActivity() instanceof MovieAdapter.MovieAdapterOnClickHandler)) {
            throw new ClassCastException("Activity must implement MovieAdapterOnClickHandler");
        }

        movieAdapter = new MovieAdapter(getContext(), (MovieAdapter.MovieAdapterOnClickHandler) getActivity());
        movieListRecyclerView.setAdapter(movieAdapter);

        movieListRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMovies(currentPage);
            }
        });

        loadMoviesOnFirstPage();
    }

    private void loadMoviesOnFirstPage() {
        loadMovies(1);
    }

    private void loadMovies(int page) {
        movieRepository.fetchPopularMovies(page)
                .doOnSubscribe(disposable -> displayLoadingStatus(page))
                .subscribe(
                        movies -> this.handleMoviesLoaded(movies, page),
                        this::handlerLoadingError,
                        this::handleLoadingComplete);
    }

    private void displayLoadingStatus(int page) {
        if (page == 1)
            showLoading();
    }

    private void handleLoadingComplete() {
        loadingProgressBar.setVisibility(View.GONE);
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
        error.printStackTrace();
    }

    private void showErrorMessage(@NonNull String message) {
        Log.e(TAG, "Error loading movies");
        errorMessageTextView.setText(message);
        loadingProgressBar.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        Log.i(TAG, "Loading movies");
        loadingProgressBar.setVisibility(View.VISIBLE);
        movieListRecyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }
}
