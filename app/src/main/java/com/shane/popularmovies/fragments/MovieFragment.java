package com.shane.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.shane.popularmovies.data.MovieContract.MovieEntry;
import com.shane.popularmovies.data.MovieDbHelper;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.Trailer;
import com.shane.popularmovies.network.MovieApi;
import com.shane.popularmovies.repositories.MovieApiRepository;
import com.shane.popularmovies.repositories.MovieRepository;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieFragment extends Fragment implements TrailerAdapter.TrailerAdapterOnClickHandler {
    public static final String TAG = MovieFragment.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.ratings_text_view) TextView ratingsTextView;
    @BindView(R.id.synopsis_text_view) TextView synopsisTextView;
    @BindView(R.id.favourite_fab) FloatingActionButton favouriteFab;
    @BindView(R.id.release_data_text_view) TextView releaseDateTextView;
    @BindView(R.id.trailer_recycler_view) RecyclerView trailerRecyclerView;
    @BindView(R.id.review_recycler_view) RecyclerView reviewRecyclerView;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private MovieRepository movieRepository;
    private Movie movie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(TAG);

        MovieApi api = MovieApi.Factory.create(getString(R.string.themoviedb_key));
        movieRepository = new MovieApiRepository(api, getContext());

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
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };

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
        this.movie = movie;
        String friendlyDate = formatDate(movie.getReleaseDate());
        toolbar.setTitle(movie.getTitle());
        synopsisTextView.setText(movie.getSynopsis());
        ratingsTextView.setText(String.valueOf(movie.getRatings()));
        releaseDateTextView.setText(friendlyDate);

        Picasso.with(getContext())
                .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                .into(posterImageView);

        int id = movie.getId();

        fetchReviews(id);
        fetchTrailers(id);

        final String selectQuery = String.format(Locale.getDefault(),
                "SELECT * FROM %s WHERE %s LIKE '%d'",
                MovieEntry.TABLE_NAME, MovieEntry.COLUMN_MOVIE_ID, movie.getId());

        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase database = sqlBrite.wrapDatabaseHelper(movieDbHelper, Schedulers.io());
        database.createQuery(MovieEntry.TABLE_NAME, selectQuery)
                .map(IS_FAVOURITE_MAPPER)
//                .mapToOneOrDefault(IS_FAVOURITE_MAPPER, Boolean.FALSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isFavourite -> {
                    Timber.d("(is favourite) movie: %b", isFavourite);
                    setIsFavouriteView(isFavourite);
                    favouriteFab.setVisibility(View.VISIBLE);
                    Timber.d("(database) %s", movie.toString());
                }, Timber::e);

//        favouriteCheck.subscribe(query -> {
//            final Cursor cursor = query.run();
//            Timber.i("favourite count: " + cursor.getCount());
//            final boolean isFavourite = cursor.getCount() > 0;
//            setIsFavouriteView(isFavourite);
//        });
    }

    static Function<SqlBrite.Query, Boolean> IS_FAVOURITE_MAPPER = query -> {
        final Cursor cursor = query.run();
        return ! (cursor == null || cursor.getCount() <= 0);
    };

//    static Function<Cursor, Boolean> IS_FAVOURITE_MAPPER = cursor -> {
//        DatabaseUtils.dumpCursor(cursor);
//        return cursor.getCount() > 0;
//    };

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
    public void onTrailerClick(@NonNull Trailer trailer) {
        final String key = trailer.getKey();

        String url = "https://www.youtube.com/watch?v=" + key;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void setIsFavouriteView(boolean isFavourite) {
        int drawableId = (isFavourite)
                ? R.drawable.ic_favorite_blue_900_24dp
                :R.drawable.ic_favorite_white_24dp;

        favouriteFab.setImageResource(drawableId);
        movie.setFavourite(isFavourite);
    }

    @OnClick(R.id.favourite_fab)
    public void onFavouriteClick(FloatingActionButton button) {
        if (movie.isFavourite()) movieRepository.removeFavourite(movie);
        else movieRepository.saveFavourite(movie);

        boolean isFavourite = ! movie.isFavourite();
        setIsFavouriteView(isFavourite);
        Timber.d("(fab clicked) %s", movie.toString());
    }
}
