package com.shane.popularmovies;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Shane on 8/5/2017.
 */

public class MovieResponse {
    @SerializedName("page") private int page;
    @SerializedName("total_results") private int totalResults;
    @SerializedName("total_pages") private int totalPages;
    @SerializedName("results") private List<Movie> movies;
}
