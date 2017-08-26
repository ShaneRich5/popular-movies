package com.shane.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shane.popularmovies.R;
import com.shane.popularmovies.activities.MovieDetailActivity;
import com.shane.popularmovies.adapters.ReviewAdapter;
import com.shane.popularmovies.adapters.TrailerAdapter;
import com.shane.popularmovies.data.MovieContract.MovieEntry;
import com.shane.popularmovies.data.MovieDbHelper;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.Trailer;
import com.shane.popularmovies.network.MovieApi;
import com.shane.popularmovies.repositories.MovieApiRepository;
import com.shane.popularmovies.repositories.MovieRepository;
import com.shane.popularmovies.utils.DateUtils;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieDetailFragment extends Fragment implements TrailerAdapter.TrailerAdapterOnClickHandler {
    public static final String TAG = MovieDetailFragment.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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

    public static MovieDetailFragment newInstance(@NonNull Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(MovieDetailActivity.EXTRA_MOVIE, movie);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(TAG);

        MovieApi api = MovieApi.Factory.create(getString(R.string.themoviedb_key));
        movieRepository = new MovieApiRepository(api, getContext());

        if (getArguments() != null) {
            movie = getArguments().getParcelable(MovieDetailActivity.EXTRA_MOVIE);
            if (movie != null) setMovie(movie);
        }
        setupLayout();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movie_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareFirstTrailer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareFirstTrailer() {
        List<Trailer> trailers = trailerAdapter.getTrailers();
        if (trailers.size() == 0) {
            Toast.makeText(getContext(), "No trailers to share", Toast.LENGTH_SHORT).show();
            return;
        }
        final Trailer trailer = trailers.get(0);

        final Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(trailer.buildVideoUrl())
                .setSubject(trailer.getName())
                .setChooserTitle("Share trailer")
                .getIntent();

        startActivity(shareIntent);
    }

    private void setupLayout() {
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

    private void setMovie(@NonNull Movie movie) {
        this.movie = movie;

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(movie.getTitle());
        }

        String friendlyDate = DateUtils.formatDate(movie.getReleaseDate());
        synopsisTextView.setText(movie.getSynopsis());
        ratingsTextView.setText(String.valueOf(movie.getRatings()));
        releaseDateTextView.setText(friendlyDate);

        Picasso.with(getContext())
                .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                .into(posterImageView);

        int id = movie.getId();

        fetchReviews(id);
        fetchTrailers(id);
        checkIfMovieIsFavoured();
    }

    private void checkIfMovieIsFavoured() {
        final String selectQuery = String.format(Locale.getDefault(),
                "SELECT * FROM %s WHERE %s LIKE '%d'",
                MovieEntry.TABLE_NAME, MovieEntry.COLUMN_MOVIE_ID, movie.getId());

        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase database = sqlBrite.wrapDatabaseHelper(movieDbHelper, Schedulers.io());
        database.createQuery(MovieEntry.TABLE_NAME, selectQuery)
                .map(IS_FAVOURITE_MAPPER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isFavourite -> {
                    setIsFavouriteView(isFavourite);
                    favouriteFab.setVisibility(View.VISIBLE);
                }, Timber::e);
    }

    static Function<SqlBrite.Query, Boolean> IS_FAVOURITE_MAPPER = query -> {
        final Cursor cursor = query.run();
        return ! (cursor == null || cursor.getCount() <= 0);
    };

    private void fetchReviews(int id) {
        compositeDisposable.add(movieRepository.fetchMovieReviews(id)
            .subscribe(
                    reviews -> reviewAdapter.setReviews(reviews),
                    this::handleReviewLoadingError));
    }

    private void fetchTrailers(int id) {
        compositeDisposable.add(movieRepository.fetchMovieTrailers(id)
            .subscribe(
                    trailers -> trailerAdapter.setTrailers(trailers),
                    this::handleReviewLoadingError));
    }

    private void handleReviewLoadingError(@NonNull Throwable error) {
        Timber.e(error);
    }

    @Override
    public void onTrailerClick(@NonNull Trailer trailer) {
        final String url = trailer.buildVideoUrl();
        final Intent intent = new Intent(Intent.ACTION_VIEW);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
