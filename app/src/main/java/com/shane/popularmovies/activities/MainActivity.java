package com.shane.popularmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    public static final String TAG = MainActivity.class.getSimpleName();

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
        addFragmentToScreen(PreferenceUtils.getShouldShowFavourites(this));

        attachFragmentByTag(MovieFavouritesFragment.TAG, savedInstanceState);
        attachFragmentByTag(MovieBrowseFragment.TAG, savedInstanceState);
    }

    private void attachFragmentByTag(@NonNull String tag, @Nullable Bundle savedInstance) {
        Fragment fragment = null;
        if (savedInstance != null) fragment = getSupportFragmentManager().findFragmentByTag(tag);

        if (tag.equals(MovieBrowseFragment.TAG)) {
            if (fragment == null) fragment = MovieBrowseFragment.newInstance();
            attachFragment(browseMovieLayout, fragment, tag);
        } else if (tag.equals(MovieFavouritesFragment.TAG)) {
            if (fragment == null) fragment = MovieFavouritesFragment.newInstance();
            attachFragment(favouriteMovieLayout, fragment, tag);
        }
    }

    private void attachFragment(@NonNull View layout, @NonNull Fragment fragment, @NonNull String tag) {
        final int viewId = layout.getId();
        final FragmentManager fragmentManager = getSupportFragmentManager();

        if (null == fragmentManager.findFragmentByTag(tag)) {
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(viewId, fragment, tag);
            fragmentTransaction.commit();
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final MovieFavouritesFragment favouritesFragment = (MovieFavouritesFragment)
                fragmentManager.findFragmentByTag(MovieFavouritesFragment.TAG);
        final MovieBrowseFragment browseFragment = (MovieBrowseFragment)
                fragmentManager.findFragmentByTag(MovieBrowseFragment.TAG);
        fragmentManager.putFragment(outState, MovieBrowseFragment.TAG, browseFragment);
        fragmentManager.putFragment(outState, MovieFavouritesFragment.TAG, favouritesFragment);
    }
}
