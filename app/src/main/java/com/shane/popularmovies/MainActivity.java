package com.shane.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{
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
}
