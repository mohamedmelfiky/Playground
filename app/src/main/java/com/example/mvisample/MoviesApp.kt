package com.example.mvisample

import android.app.Application
import com.example.mvisample.di.getModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

@FlowPreview
@ExperimentalCoroutinesApi
class MoviesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoviesApp)
            modules(getModules())
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}