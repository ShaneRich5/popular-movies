package com.shane.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    private MovieFragment movieFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        retrieveMovieFromIntent();
    }

    private void retrieveMovieFromIntent() {
        final Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra(Constants.EXTRA_MOVIE)) {
            final Movie movie = receivedIntent.getParcelableExtra(Constants.EXTRA_MOVIE);
            toolbar.setTitle(movie.getTitle());
            passMovieToFragment(movie);
        } else {
            final String errorMessage = "Error loading movie";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void passMovieToFragment(@NonNull Movie movie) {
        final FragmentManager manager = getSupportFragmentManager();
        movieFragment = (MovieFragment) manager.findFragmentById(R.id.fragment_movie);
        movieFragment.setMovie(movie);
    }
}
