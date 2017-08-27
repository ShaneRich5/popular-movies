package com.shane.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shane.popularmovies.R;
import com.shane.popularmovies.models.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 8/18/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;
    private final Context context;

    public ReviewAdapter(@NonNull Context context, @NonNull List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public ReviewAdapter(@NonNull Context context) {
        this(context, new ArrayList<>());
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_list, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        final Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(@NonNull List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public List<Review> getReviews() {
        return new ArrayList<>(reviews);
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.author_text_view) TextView authorTextView;
        @BindView(R.id.content_text_view) TextView contentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull Review review) {
            authorTextView.setText(review.getAuthor());
            contentTextView.setText(review.getContent());
        }
    }
}
