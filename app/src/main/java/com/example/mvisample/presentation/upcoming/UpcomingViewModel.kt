package com.example.mvisample.presentation.upcoming

import com.example.domain.entity.MovieLoading
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.usecases.GetUpcomingMoviesUseCase
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class UpcomingViewModel(
    machine: MoviesMachine
) : MoviesViewModel(
    machine
) {

    override suspend fun eventToAction(uiEvent: MoviesEvents): BaseAction {
        return when (uiEvent) {
            MoviesEvents.Started -> GetUpcomingMoviesUseCase.UpcomingAction.Get
            MoviesEvents.Refresh -> GetUpcomingMoviesUseCase.UpcomingAction.Refresh
            MoviesEvents.LoadMore -> GetUpcomingMoviesUseCase.UpcomingAction.NextPage
        }
    }

    override suspend fun resultToUiModel(state: MoviesUiModel, result: BaseResult): MoviesUiModel {
        return when (result) {
            is GetUpcomingMoviesUseCase.UpcomingResult.Loading -> state.copy(loadingVisibility = true)
            is GetUpcomingMoviesUseCase.UpcomingResult.Success -> state.copy(loadingVisibility = false, mainViewVisibility = true, movies = result.movies)
            is GetUpcomingMoviesUseCase.UpcomingResult.Error -> state.copy(loadingVisibility = false, errorViewVisibility = true)

            is GetUpcomingMoviesUseCase.UpcomingResult.NextPageLoading -> state.copy(isLoadingMore = true, movies = state.movies.plus(
                MovieLoading
            ))
            is GetUpcomingMoviesUseCase.UpcomingResult.NextPageSuccess -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1).plus(result.movies))
            is GetUpcomingMoviesUseCase.UpcomingResult.NextPageError -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1))

            is GetUpcomingMoviesUseCase.UpcomingResult.RefreshLoading -> state.copy(isRefreshing = true)
            is GetUpcomingMoviesUseCase.UpcomingResult.RefreshSuccess -> state.copy(isRefreshing = false, movies = result.movies)
            is GetUpcomingMoviesUseCase.UpcomingResult.RefreshError -> state.copy(isRefreshing = false)

            else -> throw IllegalArgumentException()
        }
    }

}