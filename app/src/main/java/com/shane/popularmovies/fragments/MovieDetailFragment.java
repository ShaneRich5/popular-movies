package com.shane.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.shane.popularmovies.R;
import com.shane.popularmovies.activities.MovieDetailActivity;
import com.shane.popularmovies.adapters.ReviewAdapter;
import com.shane.popularmovies.adapters.TrailerAdapter;
import com.shane.popularmovies.data.MovieContract.MovieEntry;
import com.shane.popularmovies.data.MovieDbHelper;
import com.shane.popularmovies.models.Movie;
import com.shane.popularmovies.models.Review;
import com.shane.popularmovies.models.Trailer;
import com.shane.popularmovies.network.MovieApi;
import com.shane.popularmovies.repositories.MovieApiRepository;
import com.shane.popularmovies.repositories.MovieRepository;
import com.shane.popularmovies.utils.DateUtils;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.ArrayList;
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

    public static final String SCROLL_POSITION = "scroll_position";
    public static final String TRAILER_LIST_ITEMS = "trailer_list_items";
    public static final String REVIEW_LIST_ITEMS = "review_list_items";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.scroll_view) ObservableScrollView scrollView;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.ratings_text_view) TextView ratingsTextView;
    @BindView(R.id.synopsis_text_view) TextView synopsisTextView;
    @BindView(R.id.favourite_fab) FloatingActionButton favouriteFab;
    @BindView(R.id.release_data_text_view) TextView releaseDateTextView;
    @BindView(R.id.review_recycler_view) RecyclerView reviewRecyclerView;
    @BindView(R.id.trailer_recycler_view) RecyclerView trailerRecyclerView;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private MovieRepository movieRepository;
    private Movie movie;

    static Function<SqlBrite.Query, Boolean> IS_FAVOURITE_MAPPER = query -> {
        final Cursor cursor = query.run();
        return ! (cursor == null || cursor.getCount() <= 0);
    };

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

        if (savedInstanceState == null) Timber.d("Null in onCreate");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MovieApi api = MovieApi.Factory.create(getString(R.string.themoviedb_key));
        movieRepository = new MovieApiRepository(api, getContext());

        movie = getArguments().getParcelable(MovieDetailActivity.EXTRA_MOVIE);
        setMovie(movie);


        reviewAdapter = new ReviewAdapter(getContext());
        trailerAdapter = new TrailerAdapter(getContext(), this);

        final LinearLayoutManager trailerLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        final LinearLayoutManager reviewLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };

        trailerRecyclerView.setLayoutManager(trailerLayoutManager);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);

        trailerRecyclerView.setAdapter(trailerAdapter);
        reviewRecyclerView.setAdapter(reviewAdapter);

        checkIfMovieIsFavoured();


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
        final List<Trailer> trailers = trailerAdapter.getTrailers();
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
                .load(movie.buildPosterUrl())
                .error(R.mipmap.ic_launcher)
                .into(posterImageView);
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
                .subscribe(this::setIsFavouriteView, Timber::e);
    }

    private void fetchReviews(int id) {
        compositeDisposable.add(movieRepository.fetchMovieReviews(id)
            .subscribe(this::handleReviewsLoaded, this::handleReviewLoadingError));
    }

    private void fetchTrailers(int id) {
        compositeDisposable.add(movieRepository.fetchMovieTrailers(id)
            .subscribe(this::handleTrailersLoaded, this::handleReviewLoadingError));
    }

    private void handleReviewLoadingError(@NonNull Throwable error) {
        Timber.e(error);
    }

    @Override
    public void onTrailerClick(@NonNull Trailer trailer) {
        final String videoUrl = trailer.buildVideoUrl();
        final Uri videoUri = Uri.parse(videoUrl);
        final Intent trailerIntent = new Intent(Intent.ACTION_VIEW, videoUri);

        if (trailerIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(trailerIntent);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final List<Trailer> trailers = trailerAdapter.getTrailers();
        final List<Review> reviews = reviewAdapter.getReviews();

        outState.putParcelableArrayList(TRAILER_LIST_ITEMS, new ArrayList<>(trailers));
        outState.putParcelableArrayList(REVIEW_LIST_ITEMS, new ArrayList<>(reviews));
        outState.putParcelable(SCROLL_POSITION, scrollView.onSaveInstanceState());
        Timber.d("(Saved) Position: %d %d", scrollView.getScrollX(), scrollView.getScrollY());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (savedInstanceState != null) restoreState(savedInstanceState);
        int id = movie.getId();

        if (savedInstanceState == null) {
            Timber.d("made requests, no scroll up");
            fetchReviews(id);
            fetchTrailers(id);
            return;
        }

        final Parcelable scrollState = savedInstanceState.getParcelable(SCROLL_POSITION);

        List<Trailer> trailers = savedInstanceState.getParcelableArrayList(TRAILER_LIST_ITEMS);
        List<Review> reviews = savedInstanceState.getParcelableArrayList(REVIEW_LIST_ITEMS);

        if (trailers == null) fetchTrailers(id);
        else trailerAdapter.setTrailers(trailers);

        if (reviews == null) fetchReviews(id);
        else reviewAdapter.setReviews(reviews);

        if (scrollState != null) scrollView.onRestoreInstanceState(scrollState);
    }

    private void restoreState(@NonNull Bundle savedInstanceState) {
    }

    private void handleReviewsLoaded(@NonNull List<Review> reviews) {
        reviewAdapter.setReviews(reviews);
    }

    private void handleTrailersLoaded(@NonNull List<Trailer> trailers) {
        trailerAdapter.setTrailers(trailers);
    }
}
