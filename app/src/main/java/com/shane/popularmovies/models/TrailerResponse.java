package com.shane.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Shane on 8/17/2017.
 */

public class TrailerResponse {
    @SerializedName("id") private int id;
    @SerializedName("results") private List<Trailer> trailers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> videos) {
        this.trailers = videos;
    }
}
