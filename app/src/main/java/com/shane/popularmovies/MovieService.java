package com.shane.popularmovies;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shane on 8/5/2017.
 */

public interface MovieService {
    @GET("movie/popular")
    Call<List<MovieResponse>> listPopularMovies(@Query("page") Integer page);

    @GET("movie/top_rated")
    Call<List<MovieResponse>> listTopRatedMovies(@Query("page") Integer page);

    class Factory {
        public MovieService create() {
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(MovieService.class);
        }
    }
}
