package com.shane.popularmovies.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.shane.popularmovies.models.MovieResponse;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shane on 8/5/2017.
 */

public interface MovieApi {

    @GET("movie/popular")
    Observable<MovieResponse> listPopularMovies(@Query("page") Integer page);

    @GET("movie/top_rated")
    Observable<MovieResponse> listTopRatedMovies(@Query("page") Integer page);

    class Factory {
        public static MovieApi create(@NonNull String apiKey) {
            final OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(apiKey))
                    .build();

            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            return retrofit.create(MovieApi.class);
        }
    }

    final class TokenInterceptor implements Interceptor {
        private final String token;

        TokenInterceptor(@NonNull String token) {
            this.token = token;
        }

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            final HttpUrl originalUrl = originalRequest.url();

            final HttpUrl urlWithToken = originalUrl.newBuilder()
                    .addQueryParameter("api_key", token)
                    .build();

            final Request tokenRequest = originalRequest.newBuilder()
                    .url(urlWithToken).build();

            Log.i("TokenInterceptor", tokenRequest.url().toString());

            return chain.proceed(tokenRequest);
        }
    }
}
