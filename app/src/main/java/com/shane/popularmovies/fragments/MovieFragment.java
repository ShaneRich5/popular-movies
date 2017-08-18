package com.shane.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shane.popularmovies.R;
import com.shane.popularmovies.adapters.ReviewAdapter;
import com.shane.popularmovies.adapters.TrailerAdapter;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.Trailer;
import com.shane.popularmovies.network.MovieApi;
import com.shane.popularmovies.repositories.MovieApiRepository;
import com.shane.popularmovies.repositories.MovieRepository;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MovieFragment extends Fragment implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private MovieRepository movieRepository;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.ratings_text_view) TextView ratingsTextView;
    @BindView(R.id.synopsis_text_view) TextView synopsisTextView;
    @BindView(R.id.release_data_text_view) TextView releaseDateTextView;
    @BindView(R.id.trailer_recycler_view) RecyclerView trailerRecyclerView;
    @BindView(R.id.review_recycler_view) RecyclerView reviewRecyclerView;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, view);

        MovieApi api = MovieApi.Factory.create(getString(R.string.themoviedb_key));
        movieRepository = new MovieApiRepository(api);

        setupLayout();
        return view;
    }

    private void setupLayout() {
        setupToolbar();

        reviewAdapter = new ReviewAdapter(getContext());
        trailerAdapter = new TrailerAdapter(getContext(), this);


        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager verticalLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        trailerRecyclerView.setLayoutManager(horizontalLayoutManager);
        reviewRecyclerView.setLayoutManager(verticalLayoutManager);

        trailerRecyclerView.setAdapter(trailerAdapter);
        reviewRecyclerView.setAdapter(reviewAdapter);
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
        String friendlyDate = formatDate(movie.getTitle());
        toolbar.setTitle(friendlyDate);
        synopsisTextView.setText(movie.getSynopsis());
        ratingsTextView.setText(String.valueOf(movie.getRatings()));
        releaseDateTextView.setText(movie.getReleaseDate());

        Picasso.with(getContext())
                .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                .into(posterImageView);

        int id = movie.getId();

        fetchReviews(id);
        fetchTrailers(id);
    }

    private void fetchReviews(@NonNull int id) {
        movieRepository.fetchMovieReviews(id)
            .subscribe(
                    reviews -> reviewAdapter.setReviews(reviews),
                    this::handleReviewLoadingError);
    }


    private void fetchTrailers(@NonNull int id) {
        movieRepository.fetchMovieTrailers(id)
            .subscribe(
                    trailers -> trailerAdapter.setTrailers(trailers),
                    this::handleReviewLoadingError
            );
    }

    private void handleReviewLoadingError(@NonNull Throwable error) {
        Timber.e(error);
    }

    public String formatDate(@NonNull String rawDate) {
        try {
            final SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-M-dd", Locale.US);
            final SimpleDateFormat targetFormat = new SimpleDateFormat("M dd, yyyy", Locale.US);
            final Date date = originalFormat.parse(rawDate);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return rawDate;
        }
    }

    @Override
    public void onClick(@NonNull Trailer trailer) {

    }
}
