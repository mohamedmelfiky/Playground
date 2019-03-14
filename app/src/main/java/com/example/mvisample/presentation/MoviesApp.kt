package com.example.mvisample.presentation

import android.app.Application
import com.example.mvisample.BuildConfig
import com.example.mvisample.data.remote.BASE_URL
import com.example.mvisample.data.remote.getApiService
import com.example.mvisample.data.remote.getHttpClient
import com.example.mvisample.data.repos.MoviesRepo
import com.example.mvisample.domain.repos.IMoviesRepo
import com.example.mvisample.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.mvisample.presentation.base.BaseViewModel
import com.example.mvisample.presentation.movies.MoviesAction
import com.example.mvisample.presentation.movies.MoviesResult
import com.example.mvisample.presentation.movies.MoviesState
import com.example.mvisample.presentation.movies.MoviesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.DebugTree

val moviesModule = module {
    factory { getHttpClient() }
    factory { getApiService(BASE_URL, get()) }
    single<IMoviesRepo> { MoviesRepo(get()) }
    factory { GetNowPlayingMoviesUseCase(get()) }
    viewModel<BaseViewModel<MoviesAction, MoviesResult, MoviesState>> {
        MoviesViewModel(
            get()
        )
    }
}

class MoviesApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MoviesApp)
            modules(moviesModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}