package com.shane.popularmovies.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shane on 8/17/2017.
 */

public class Review {
    @SerializedName("id") private String id;
    @SerializedName("url") private String url;
    @SerializedName("author") private String author;
    @SerializedName("content") private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
