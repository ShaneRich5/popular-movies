package com.shane.popularmovies;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Shane on 8/7/2017.
 */

public class PopularMoviesApp extends Application {

    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
