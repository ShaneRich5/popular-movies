package com.shane.popularmovies.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    public static final String MOVIE_DB_API_URL = "https://api.themoviedb.org/3/";

    private final String apiKey;

    public NetworkModule(@NonNull String apiKey) {
        this.apiKey = apiKey;
    }

    @Provides
    @Singleton
    public MovieApi.TokenInterceptor provideTokenInterceptor() {
        return new MovieApi.TokenInterceptor(apiKey);
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(MovieApi.TokenInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Gson gson, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(MOVIE_DB_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    MovieApi provideMovieApi(Retrofit retrofit) {
        return retrofit.create(MovieApi.class);
    }

}
