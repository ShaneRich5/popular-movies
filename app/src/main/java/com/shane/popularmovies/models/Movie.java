package com.shane.popularmovies.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.shane.popularmovies.data.MovieContract.MovieEntry;

import java.util.Locale;

/**
 * Created by Shane on 8/1/2017.
 */

public class Movie implements Parcelable {

    @SerializedName("id") private int id;
    @SerializedName("title") private String title;
    @SerializedName("poster_path") private String posterPath;
    @SerializedName("overview") private String synopsis;
    @SerializedName("vote_average") private double ratings;
    @SerializedName("release_date") private String releaseDate;

    private boolean isFavourite = false;

    public Movie(int id, String title, String posterPath, String synopsis, double ratings, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.ratings = ratings;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(title);
        out.writeString(posterPath);
        out.writeString(synopsis);
        out.writeDouble(ratings);
        out.writeString(releaseDate);
        out.writeByte((byte) (isFavourite ? 1 : 0));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        ratings = in.readDouble();
        releaseDate = in.readString();
        isFavourite = in.readByte() != 0;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Movie: {%d, %s, %b}", id, title, isFavourite);
    }

    public String buildPosterUrl() {
        return "http://image.tmdb.org/t/p/w185/" + getPosterPath();
    }

    public static final class Builder implements Buildable {
        private final ContentValues values = new ContentValues();

        public Builder() {}

        public Builder id(int id) {
            values.put(MovieEntry.COLUMN_MOVIE_ID, id);
            return this;
        }

        public Builder title(String title) {
            values.put(MovieEntry.COLUMN_TITLE, title);
            return this;
        }

        public Builder posterPath(String posterPath) {
            values.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
            return this;
        }

        public Builder synopsis(String synopsis) {
            values.put(MovieEntry.COLUMN_SYNOPSIS, synopsis);
            return this;
        }

        public Builder ratings(double ratings) {
            values.put(MovieEntry.COLUMN_RATINGS, ratings);
            return this;
        }

        public Builder releaseDate(String releaseDate) {
            values.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            return this;
        }

        @Override
        public ContentValues build() {
            return values;
        }
    }
}
