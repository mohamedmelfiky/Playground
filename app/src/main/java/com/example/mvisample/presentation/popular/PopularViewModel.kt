package com.example.mvisample.presentation.popular

import com.example.domain.entity.MovieLoading
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.usecases.GetPopularMoviesUseCase
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class PopularViewModel(
    machine: MoviesMachine
) : MoviesViewModel(
    machine
) {

    override suspend fun eventToAction(uiEvent: MoviesEvents): BaseAction {
        return when (uiEvent) {
            MoviesEvents.Started -> GetPopularMoviesUseCase.PopularMoviesAction.Get
            MoviesEvents.Refresh -> GetPopularMoviesUseCase.PopularMoviesAction.Refresh
            MoviesEvents.LoadMore -> GetPopularMoviesUseCase.PopularMoviesAction.NextPage
        }
    }

    override suspend fun resultToUiModel(state: MoviesUiModel, result: BaseResult): MoviesUiModel {
        return when (result) {
            is GetPopularMoviesUseCase.PopularMoviesResult.Loading -> state.copy(loadingVisibility = true)
            is GetPopularMoviesUseCase.PopularMoviesResult.Success -> state.copy(loadingVisibility = false, mainViewVisibility = true, movies = result.movies)
            is GetPopularMoviesUseCase.PopularMoviesResult.Error -> state.copy(loadingVisibility = false, errorViewVisibility = true)

            is GetPopularMoviesUseCase.PopularMoviesResult.NextPageLoading -> state.copy(isLoadingMore = true, movies = state.movies.plus(MovieLoading))
            is GetPopularMoviesUseCase.PopularMoviesResult.NextPageSuccess -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1).plus(result.movies))
            is GetPopularMoviesUseCase.PopularMoviesResult.NextPageError -> state.copy(isLoadingMore = false, movies = state.movies.dropLast(1))

            is GetPopularMoviesUseCase.PopularMoviesResult.RefreshLoading -> state.copy(isRefreshing = true)
            is GetPopularMoviesUseCase.PopularMoviesResult.RefreshSuccess -> state.copy(isRefreshing = false, movies = result.movies)
            is GetPopularMoviesUseCase.PopularMoviesResult.RefreshError -> state.copy(isRefreshing = false)

            else -> throw IllegalArgumentException()
        }
    }

}