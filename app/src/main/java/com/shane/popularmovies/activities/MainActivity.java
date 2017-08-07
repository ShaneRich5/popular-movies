package com.shane.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shane.popularmovies.R;
import com.shane.popularmovies.adapters.MovieAdapter;
import com.shane.popularmovies.fragments.MovieListFragment;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static String TAG = MainActivity.class.getSimpleName();

    private MovieListFragment movieListFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        movieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_list);
    }

    @Override
    public void onClick(@NonNull Movie movie) {
        final Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_sort);
        Spinner sortOptionsSpinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.sort_order_array, R.layout.item_sort_option);

        adapter.setDropDownViewResource(R.layout.item_sort_by_dropdown);

        sortOptionsSpinner.setAdapter(adapter);

        return true;
    }
}
