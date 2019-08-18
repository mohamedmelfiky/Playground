package com.example.mvisample.presentation.movies

import com.example.domain.udf.BaseAction
import com.example.domain.udf.Logger
import com.example.domain.udf.Machine
import com.example.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.domain.usecases.GetPopularMoviesUseCase
import com.example.domain.usecases.GetTopRatedMoviesUseCase
import com.example.domain.usecases.GetUpcomingMoviesUseCase
import com.example.mvisample.util.AndroidLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class MoviesMachine(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    androidLogger: Logger
) : Machine(logger = androidLogger) {

    override suspend fun actOnAction(action: BaseAction) =
        when (action) {
            is GetPopularMoviesUseCase.PopularMoviesAction -> getPopularMoviesUseCase.invoke(action)
            is GetTopRatedMoviesUseCase.TopRatedAction -> getTopRatedMoviesUseCase.invoke(action)
            is GetUpcomingMoviesUseCase.UpcomingAction -> getUpcomingMoviesUseCase.invoke(action)
            is GetNowPlayingMoviesUseCase.NowPlayingAction -> getNowPlayingMoviesUseCase.invoke(action)
            else -> throw IllegalArgumentException()
        }

}