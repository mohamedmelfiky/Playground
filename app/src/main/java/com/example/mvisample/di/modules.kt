package com.example.mvisample.di

import com.example.data.BuildConfig
import com.example.data.remote.Api
import com.example.data.remote.createNetworkClient
import com.example.data.repos.MoviesRepo
import com.example.domain.repos.IMoviesRepo
import com.example.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.mvisample.presentation.base.BaseViewModel
import com.example.mvisample.presentation.movies.MoviesAction
import com.example.mvisample.presentation.movies.MoviesResult
import com.example.mvisample.presentation.movies.MoviesState
import com.example.mvisample.presentation.movies.MoviesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

const val BASE_URL = "https://api.themoviedb.org/3/"

private val retrofit: Retrofit = createNetworkClient(BASE_URL, BuildConfig.DEBUG)
private val moviesApi = retrofit.create(Api::class.java)

private val networkModule = module {
    factory { moviesApi }
}

private val repositoryModule = module {
    single<IMoviesRepo> { MoviesRepo(get()) }
}

private val useCaseModule = module {
    factory { GetNowPlayingMoviesUseCase(get()) }
}

private val viewModelModule = module {
    viewModel<BaseViewModel<MoviesAction, MoviesResult, MoviesState>> {
        MoviesViewModel(
            get()
        )
    }
}

fun getModules(): Array<Module> {
    return arrayOf(
        networkModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )
}