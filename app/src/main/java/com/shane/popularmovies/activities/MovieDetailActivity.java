package com.shane.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.shane.popularmovies.R;
import com.shane.popularmovies.fragments.MovieDetailFragment;
import com.shane.popularmovies.models.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fragment_movie) FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupToolbar();
        createFragmentWithMovieFromIntent();
        retrieveMovieFromIntent();
    }

    private void createFragmentWithMovieFromIntent() {
        if (getIntent().hasExtra(MovieDetailActivity.EXTRA_MOVIE)) {
            final Movie movie = retrieveMovieFromIntent();
            createMovieFragment(movie);
        } else {
            final String errorMessage = "Error loading movie";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            Timber.d("Movie not retrieved from intent");
            finish();
        }
    }

    private void createMovieFragment(@NonNull Movie movie) {
        getSupportFragmentManager().beginTransaction()
                .replace(frameLayout.getId(), MovieDetailFragment.newInstance(movie), MovieDetailFragment.TAG)
                .commit();
    }

    private void setupToolbar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private Movie retrieveMovieFromIntent() {
        return getIntent().getParcelableExtra(MovieDetailActivity.EXTRA_MOVIE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            final Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
