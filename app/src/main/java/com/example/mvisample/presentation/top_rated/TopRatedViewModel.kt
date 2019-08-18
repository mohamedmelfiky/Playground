package com.example.mvisample.presentation.top_rated

import com.example.domain.entity.MovieLoading
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.usecases.GetTopRatedMoviesUseCase
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TopRatedViewModel(
    machine: MoviesMachine
) : MoviesViewModel(
    machine
) {

    override suspend fun eventToAction(uiEvent: MoviesEvents): BaseAction {
        return when (uiEvent) {
            MoviesEvents.Started -> GetTopRatedMoviesUseCase.TopRatedAction.Get
            MoviesEvents.Refresh -> GetTopRatedMoviesUseCase.TopRatedAction.Refresh
            MoviesEvents.LoadMore -> GetTopRatedMoviesUseCase.TopRatedAction.NextPage
        }
    }

    override suspend fun resultToUiModel(state: MoviesUiModel, result: BaseResult): MoviesUiModel {
        return when (result) {
            is GetTopRatedMoviesUseCase.TopRatedResult.Loading -> state.copy(loadingVisibility = true)
            is GetTopRatedMoviesUseCase.TopRatedResult.Success -> state.copy(loadingVisibility = false, mainViewVisibility = true, movies = result.movies)
            is GetTopRatedMoviesUseCase.TopRatedResult.Error -> state.copy(loadingVisibility = false, errorViewVisibility = true)

            is GetTopRatedMoviesUseCase.TopRatedResult.NextPageLoading -> state.copy(isLoadingMore = true, movies = state.movies.plus(MovieLoading))
            is GetTopRatedMoviesUseCase.TopRatedResult.NextPageSuccess -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1).plus(result.movies))
            is GetTopRatedMoviesUseCase.TopRatedResult.NextPageError -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1))

            is GetTopRatedMoviesUseCase.TopRatedResult.RefreshLoading -> state.copy(isRefreshing = true)
            is GetTopRatedMoviesUseCase.TopRatedResult.RefreshSuccess -> state.copy(isRefreshing = false, movies = result.movies)
            is GetTopRatedMoviesUseCase.TopRatedResult.RefreshError -> state.copy(isRefreshing = false)

            else -> throw IllegalArgumentException()
        }
    }

}