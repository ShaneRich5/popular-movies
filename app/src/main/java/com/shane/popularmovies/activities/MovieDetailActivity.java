package com.shane.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.shane.popularmovies.R;
import com.shane.popularmovies.fragments.MovieFragment;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.utils.Constants;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MovieDetailActivity extends AppCompatActivity {


    private MovieFragment movieFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        retrieveMovieFromIntent();
    }

    private void retrieveMovieFromIntent() {
        final Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra(Constants.EXTRA_MOVIE)) {
            final Movie movie = receivedIntent.getParcelableExtra(Constants.EXTRA_MOVIE);
            passMovieToFragment(movie);
        } else {
            final String errorMessage = "Error loading movie";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            Timber.d("Movie not retrieved from intent");
            finish();
        }
    }

    private void passMovieToFragment(@NonNull Movie movie) {
        final FragmentManager manager = getSupportFragmentManager();
        movieFragment = (MovieFragment) manager.findFragmentById(R.id.fragment_movie);
        movieFragment.setMovie(movie);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(movie.getTitle());
        }
    }
}
