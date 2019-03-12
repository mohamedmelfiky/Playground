package com.example.mvisample.presentation

import android.app.Application
import com.example.mvisample.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree



class MoviesApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}