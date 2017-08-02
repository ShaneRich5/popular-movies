package com.shane.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    final static String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    final static String PARAM_PAGE = "page";
    final static String PARAM_LANGUAGE = "language";
    final static String PARAM_API_KEY = "api_key";

    private String languageEnglish = "en-US";
    private String pageOne = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMovieList();
    }

    private void loadMovieList() {
        final URL url = buildUrl();
        if (url == null) return;
        new FetchMoviesTask().execute(url.toString());
    }

    private URL buildUrl() {
        final Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_PAGE, pageOne)
                .appendQueryParameter(PARAM_LANGUAGE, languageEnglish)
                .appendQueryParameter(PARAM_API_KEY, getResources().getString(R.string.themoviedb_key))
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        public FetchMoviesTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... urls) {
            List<Movie> movies = new ArrayList<>();

            if (urls.length == 0) return movies;
            String url = urls[0];
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url).build();

            String result = null;

            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }

            try {
                JSONObject responseData = new JSONObject(result);
                JSONArray movieListData = responseData.getJSONArray("results");

                for (int i = 0; i < movieListData.length(); i++) {
                    JSONObject movieData = movieListData.getJSONObject(i);
                    int id = movieData.getInt("id");
                    String title = movieData.getString("title");
                    double rating = movieData.getDouble("vote_average");
                    String posterPath = movieData.getString("poster_path");
                    String synopsis = movieData.getString("overview");
                    String releaseDate = movieData.getString("release_date");

                    Movie movie = new Movie(id, title, posterPath, synopsis, rating, releaseDate);
                    movies.add(movie);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

            return movies;
        }



        @Override
        protected void onPostExecute(List<Movie> movies) {
            Log.i(TAG, movies.toString());
        }
    }
}
