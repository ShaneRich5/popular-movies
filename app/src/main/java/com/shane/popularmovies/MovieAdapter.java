package com.shane.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 8/2/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final Context context;
    private MovieAdapterOnClickHandler clickHandler;
    private List<Movie> movies;


    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(@NonNull Context context, @NonNull MovieAdapterOnClickHandler clickHandler, @NonNull List<Movie> movies) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.movies = movies;
    }

    public MovieAdapter(@NonNull Context context, @NonNull MovieAdapterOnClickHandler clickHandler) {
        this(context, clickHandler, new ArrayList<Movie>());
    }

    public void setMovies(List<Movie> newMovieList) {
        movies = newMovieList;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.poster_image_view) ImageView posterImageView;

        private Context context;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        void bind(@NonNull Movie movie) {
            Picasso.with(context)
                    .load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                    .into(posterImageView);
        }

        @Override
        public void onClick(View view) {
            final int index = getAdapterPosition();
            final Movie movie = movies.get(index);
            clickHandler.onClick(movie);
        }
    }
}
