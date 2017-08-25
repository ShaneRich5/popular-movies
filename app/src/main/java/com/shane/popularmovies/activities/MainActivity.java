package com.shane.popularmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.shane.popularmovies.R;
import com.shane.popularmovies.adapters.MovieAdapter;
import com.shane.popularmovies.data.MovieContract;
import com.shane.popularmovies.fragments.MovieListFragment;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private MovieListFragment movieListFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        DatabaseUtils.dumpCursor(cursor);
        if (cursor != null) cursor.close();

        setSupportActionBar(toolbar);
        movieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                final Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onClick(@NonNull Movie movie) {
        final Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MOVIE, movie);
        startActivity(intent);
    }
}
