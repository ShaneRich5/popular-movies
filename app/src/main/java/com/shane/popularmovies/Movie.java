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
}
