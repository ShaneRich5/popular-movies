package com.shane.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Shane on 8/25/2017.
 */

public class MovieFavouritesFragment extends MovieListFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMoviesFromCache();

    }

    private void loadMoviesFromCache() {
        movieRepository.
    }

    @Override
    public void onRefresh() {

    }
}
