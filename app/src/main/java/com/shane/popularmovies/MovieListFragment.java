package com.shane.popularmovies;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {
    private String TAG = MovieListFragment.class.getSimpleName();

    private MovieAdapter movieAdapter;
    private MovieRepository movieRepository;

    @BindView(R.id.movie_list_recycler) RecyclerView movieListRecyclerView;
    @BindView(R.id.load_progress_bar) ProgressBar loadingProgressBar;
    @BindView(R.id.error_message_text_view) TextView errorMessageTextView;

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
                movieRepository.fetchPopularMovies(currentPage)
                        .subscribe(
                                movies -> {
                                    movieAdapter.addMovies(movies);
                                },
                                error-> {
                                    Log.e(TAG, error.getMessage());
                                }
                        );
            }
        });

        movieRepository.fetchPopularMovies(1)
                .doOnSubscribe(disposable -> showLoading())
                .subscribe(movies -> {
                            movieListRecyclerView.setVisibility(View.VISIBLE);
                            movieAdapter.setMovies(movies);
                        },
                        error -> showErrorMessage(error.getMessage()),
                        () -> loadingProgressBar.setVisibility(View.GONE));
    }

    private void loadMoviesOnPage(int currentPage) {

    }

    private void showErrorMessage(@NonNull String message) {
        Log.e(TAG, "Error loading movies");
        errorMessageTextView.setText(message);
        loadingProgressBar.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        Log.i(TAG, "Loading movies");
        loadingProgressBar.setVisibility(View.VISIBLE);
        movieListRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
    }
}
