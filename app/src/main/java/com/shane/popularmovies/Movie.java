package com.shane.popularmovies;

/**
 * Created by Shane on 8/1/2017.
 */

public class Movie {
    private int id;
    private String title;
    private String posterPath;
    private String synopsis;
    private double rating;
    private String releaseDate;

    public Movie(int id, String title, String posterPath, String synopsis, double rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.rating = rating;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
