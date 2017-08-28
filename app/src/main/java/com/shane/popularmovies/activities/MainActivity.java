package com.shane.popularmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.shane.popularmovies.R;
import com.shane.popularmovies.adapters.MovieAdapter;
import com.shane.popularmovies.fragments.MovieBrowseFragment;
import com.shane.popularmovies.fragments.MovieFavouritesFragment;
import com.shane.popularmovies.fragments.SortOrderDialogFragment;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.utils.PreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fragment_browse_movie_list) FrameLayout browseMovieLayout;
    @BindView(R.id.fragment_favourite_movie_list) FrameLayout favouriteMovieLayout;

    Switch favouriteSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(favouriteMovieLayout.getId(), MovieFavouritesFragment.newInstance());
            fragmentTransaction.replace(browseMovieLayout.getId(), MovieBrowseFragment.newInstance());
            fragmentTransaction.commit();
        }

        addFragmentToScreen(PreferenceUtils.getShouldShowFavourites(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addFragmentToScreen(PreferenceUtils.getShouldShowFavourites(this));
    }

    private void addFragmentToScreen(boolean shouldShowFavourites) {
        browseMovieLayout.setVisibility((shouldShowFavourites) ? View.GONE : View.VISIBLE);
        favouriteMovieLayout.setVisibility((shouldShowFavourites) ? View.VISIBLE : View.GONE);
        if (favouriteSwitch != null) favouriteSwitch.setChecked(shouldShowFavourites);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                SortOrderDialogFragment sortOrderFragment = new SortOrderDialogFragment();
                sortOrderFragment.show(getSupportFragmentManager(), SortOrderDialogFragment.TAG);
                return true;
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        RelativeLayout switchContainer = (RelativeLayout) menu.findItem(R.id.action_toggle_favourite).getActionView();
        favouriteSwitch = (Switch) switchContainer.findViewById(R.id.toggle_switch);
        favouriteSwitch.setChecked(PreferenceUtils.getShouldShowFavourites(this));
        favouriteSwitch.setOnCheckedChangeListener((compoundButton, isChecked) ->
                PreferenceUtils.setShouldShowFavourites(this, isChecked));
        return true;
    }

    @Override
    public void onClick(@NonNull Movie movie) {
        final Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String favouriteKey = getString(R.string.pref_favourite_key);

        if (key.equals(favouriteKey)) {
            boolean shouldShowFavourite = PreferenceUtils.getShouldShowFavourites(this);
            addFragmentToScreen(shouldShowFavourite);
        }
    }
}
