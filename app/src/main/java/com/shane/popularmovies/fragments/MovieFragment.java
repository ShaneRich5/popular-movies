package com.shane.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shane.popularmovies.R;
import com.shane.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieFragment extends Fragment {
    public static final String TAG = MovieFragment.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.ratings_text_view) TextView ratingsTextView;
    @BindView(R.id.synopsis_text_view) TextView synopsisTextView;
    @BindView(R.id.release_data_text_view) TextView releaseDateTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, view);
        setupToolbar();
        return view;
    }

    private void setupToolbar() {
        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        final ActionBar activityActionBar = activity.getSupportActionBar();

        if (activityActionBar == null) {
            activity.setSupportActionBar(toolbar);

            final ActionBar fragmentActionBar = activity.getSupportActionBar();

            if (fragmentActionBar != null) {
                fragmentActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public void setMovie(@NonNull Movie movie) {
        toolbar.setTitle(movie.getTitle());
        titleTextView.setText(movie.getTitle());
        synopsisTextView.setText(movie.getSynopsis());
        ratingsTextView.setText(String.valueOf(movie.getRatings()));
        releaseDateTextView.setText(movie.getReleaseDate());

        Picasso.with(getContext())
                .load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                .into(posterImageView);
    }
}
