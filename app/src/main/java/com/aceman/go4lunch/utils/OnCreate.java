package com.aceman.go4lunch.utils;

import android.app.Application;

import com.aceman.go4lunch.BuildConfig;

import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class OnCreate extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }
}
