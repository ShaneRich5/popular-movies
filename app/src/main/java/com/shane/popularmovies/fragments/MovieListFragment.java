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
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;


public class MovieListFragment extends Fragment {
    public static String TAG = MovieListFragment.class.getSimpleName();

    public static String POPULAR_SORT_ORDER = "popular";
    public static String TOP_RATED_SORT_ORDER = "top_rated";

    private MovieAdapter movieAdapter;
    private MovieRepository movieRepository;

    private String sortOrder = POPULAR_SORT_ORDER;

    @BindView(R.id.movie_list_recycler) RecyclerView movieListRecyclerView;
    @BindView(R.id.load_progress_bar) ProgressBar loadingProgressBar;
    @BindView(R.id.error_message_text_view) TextView errorMessageTextView;
    @BindView(R.id.error_layout) ConstraintLayout errorLayout;

    private EndlessRecyclerOnScrollListener scrollListener;

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

        scrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMovies(currentPage);
            }
        };

        movieListRecyclerView.addOnScrollListener(scrollListener);
        loadMoviesOnFirstPage();
    }

    private void loadMoviesOnFirstPage() {
        loadMovies(1);
    }

    public void sortOrderChanged(@NonNull String newSortOrder) {
        if (sortOrder.equals(newSortOrder)) return;
        sortOrder = newSortOrder;
        movieAdapter.clearMovies();
        scrollListener.reset();
        loadMoviesOnFirstPage();
    }

    private void loadMovies(int page) {
        Observable<List<Movie>> observable;

        if (sortOrder.equals(POPULAR_SORT_ORDER)) {
            observable = movieRepository.fetchPopularMovies(page);
        } else {
            observable = movieRepository.fetchTopRatedMovies(page);
        }

        observable.doOnSubscribe(disposable -> showLoading())
                .subscribe(
                        movies -> this.handleMoviesLoaded(movies, page),
                        this::handlerLoadingError,
                        this::handleLoadingComplete);
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
        movieListRecyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        Log.i(TAG, "Loading movies");
        loadingProgressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }
}
