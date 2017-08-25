package com.shane.popularmovies.network;

import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.shane.popularmovies.models.MovieResponse;
import com.shane.popularmovies.models.ReviewResponse;
import com.shane.popularmovies.models.TrailerResponse;

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
import retrofit2.http.Path;
import retrofit2.http.Query;
import timber.log.Timber;

/**
 * Created by Shane on 8/5/2017.
 */

public interface MovieApi {

    @GET("movie/{sortOrder}")
    Observable<MovieResponse> getMovies(@Path("sortOrder") String SortOrder, @Query("page") Integer page);

    @GET("movie/{movie}/reviews")
    Observable<ReviewResponse> getReviews(@Path("movie") Integer id);

    @GET("movie/{movie}/videos")
    Observable<TrailerResponse> getTrailers(@Path("movie") Integer id);

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

            Timber.i(urlWithToken.toString());

            final Request tokenRequest = originalRequest.newBuilder()
                    .url(urlWithToken).build();

            return chain.proceed(tokenRequest);
        }
    }
}
