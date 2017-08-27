package com.shane.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shane on 8/17/2017.
 */

public class Trailer implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("site")
    private String site;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String buildVideoUrl() {
        return "https://www.youtube.com/watch?v=" + key;
    }

    public String buildThumbnailUrl() {
        return "http://img.youtube.com/vi/" + key + "/hqdefault.jpg";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(id);
        out.writeString(key);
        out.writeString(site);
        out.writeString(name);
    }

    private Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
        site = in.readString();
        name = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
