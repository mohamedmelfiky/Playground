package com.example.mvisample.presentation.now_playing

import com.example.domain.entity.MovieLoading
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class NowPlayingViewModel(
    machine: MoviesMachine
) : MoviesViewModel(machine) {

    override suspend fun eventToAction(uiEvent: MoviesEvents): BaseAction {
        return when (uiEvent) {
            MoviesEvents.Started -> GetNowPlayingMoviesUseCase.NowPlayingAction.Get
            MoviesEvents.Refresh -> GetNowPlayingMoviesUseCase.NowPlayingAction.Refresh
            MoviesEvents.LoadMore -> GetNowPlayingMoviesUseCase.NowPlayingAction.NextPage
        }
    }

    override suspend fun resultToUiModel(state: MoviesUiModel, result: BaseResult): MoviesUiModel {
        return when (result) {
            is GetNowPlayingMoviesUseCase.NowPlayingResult.Loading -> state.copy(loadingVisibility = true)
            is GetNowPlayingMoviesUseCase.NowPlayingResult.Success -> state.copy(loadingVisibility = false, mainViewVisibility = true, movies = result.movies)
            is GetNowPlayingMoviesUseCase.NowPlayingResult.Error -> state.copy(loadingVisibility = false, errorViewVisibility = true)

            is GetNowPlayingMoviesUseCase.NowPlayingResult.NextPageLoading -> state.copy(isLoadingMore = true, movies = state.movies.plus(
                MovieLoading
            ))
            is GetNowPlayingMoviesUseCase.NowPlayingResult.NextPageSuccess -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1).plus(result.movies))
            is GetNowPlayingMoviesUseCase.NowPlayingResult.NextPageError -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1))

            is GetNowPlayingMoviesUseCase.NowPlayingResult.RefreshLoading -> state.copy(isRefreshing = true)
            is GetNowPlayingMoviesUseCase.NowPlayingResult.RefreshSuccess -> state.copy(isRefreshing = false, movies = result.movies)
            is GetNowPlayingMoviesUseCase.NowPlayingResult.RefreshError -> state.copy(isRefreshing = false)

            else -> throw IllegalArgumentException()
        }
    }

}