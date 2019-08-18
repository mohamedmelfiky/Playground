package com.example.mvisample.di

import com.example.data.BuildConfig
import com.example.data.remote.RemoteDataSource
import com.example.data.remote.api.Api
import com.example.data.remote.api.createNetworkClient
import com.example.data.repos.MoviesRepo
import com.example.domain.repos.IMoviesRepo
import com.example.domain.udf.Logger
import com.example.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.domain.usecases.GetPopularMoviesUseCase
import com.example.domain.usecases.GetTopRatedMoviesUseCase
import com.example.domain.usecases.GetUpcomingMoviesUseCase
import com.example.mvisample.presentation.movies.MoviesMachine
import com.example.mvisample.presentation.now_playing.NowPlayingViewModel
import com.example.mvisample.presentation.popular.PopularViewModel
import com.example.mvisample.presentation.top_rated.TopRatedViewModel
import com.example.mvisample.presentation.upcoming.UpcomingViewModel
import com.example.mvisample.util.AndroidLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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

private val remoteDataSourceModule = module {
    factory { RemoteDataSource(get()) }
}

private val repositoryModule = module {
    single<IMoviesRepo> { MoviesRepo(get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
private val useCaseModule = module {
    factory { GetPopularMoviesUseCase(get()) }
    factory { GetTopRatedMoviesUseCase(get()) }
    factory { GetNowPlayingMoviesUseCase(get()) }
    factory { GetUpcomingMoviesUseCase(get()) }
}

private val logger = module {
    factory<Logger> { AndroidLogger() }
}

@ExperimentalCoroutinesApi
@FlowPreview
private val machines = module {
    factory { MoviesMachine(get(), get(), get(), get(), get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
private val viewModelModule = module {
    viewModel { PopularViewModel(get()) }
    viewModel { TopRatedViewModel(get()) }
    viewModel { NowPlayingViewModel(get()) }
    viewModel { UpcomingViewModel(get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
fun getModules(): Array<Module> {
    return arrayOf(
        networkModule,
        remoteDataSourceModule,
        repositoryModule,
        useCaseModule,
        logger,
        machines,
        viewModelModule
    )
}