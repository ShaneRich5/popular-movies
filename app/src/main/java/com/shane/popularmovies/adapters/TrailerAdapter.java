package com.shane.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shane.popularmovies.R;
import com.shane.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 8/18/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private TrailerAdapterOnClickHandler clickHandler;
    private final Context context;
    private List<Trailer> trailers;

    public interface TrailerAdapterOnClickHandler{
        void onClick(@NonNull Trailer trailer);
    }

    public TrailerAdapter(@NonNull Context context, @NonNull TrailerAdapterOnClickHandler clickHandler, @NonNull List<Trailer> trailers) {
        this.clickHandler = clickHandler;
        this.context = context;
        this.trailers = trailers;
    }

    public TrailerAdapter(@NonNull Context context, @NonNull TrailerAdapterOnClickHandler clickHandler) {
        this(context, clickHandler, new ArrayList<>());
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trailer_list, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        final Trailer trailer = trailers.get(position);
        holder.bind(trailer);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void setTrailers(@NonNull List<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.thumbnail_image_view) ImageView thumbnailImageView;
        @BindView(R.id.name_text_view) TextView nameTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Trailer trailer) {
            nameTextView.setText(trailer.getName());

            final String key = trailer.getKey();

            Picasso.with(context)
                    .load("http://img.youtube.com/vi/" + key + "/hqdefault.jpg")
                    .into(thumbnailImageView);
        }

        @Override
        public void onClick(View view) {
            final int index = getAdapterPosition();
            final Trailer trailer = trailers.get(index);
            clickHandler.onClick(trailer);
        }
    }
}
