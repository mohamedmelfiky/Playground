package com.example.domain.usecases

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.repos.IMoviesRepo
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.udf.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.lang.Exception

@ExperimentalCoroutinesApi
class GetPopularMoviesUseCase(
    private val moviesRepo: IMoviesRepo
) : BaseUseCase<GetPopularMoviesUseCase.PopularMoviesAction, GetPopularMoviesUseCase.PopularMoviesResult>(){

    sealed class PopularMoviesAction : BaseAction {
        object Get: PopularMoviesAction()
        object NextPage: PopularMoviesAction()
        object Refresh: PopularMoviesAction()
    }

    sealed class PopularMoviesResult : BaseResult {
        object Loading : PopularMoviesResult()
        data class Success(val movies: List<Movie>) : PopularMoviesResult()
        data class Error(val exception: Exception) : PopularMoviesResult()

        object RefreshLoading : PopularMoviesResult()
        data class RefreshSuccess(val movies: List<Movie>) : PopularMoviesResult()
        data class RefreshError(val error: Throwable) : PopularMoviesResult()

        object NextPageLoading : PopularMoviesResult()
        data class NextPageSuccess(val movies: List<Movie>) : PopularMoviesResult()
        data class NextPageError(val error: Throwable) : PopularMoviesResult()
    }

    private var currentPage: Int = 1

    override fun invoke(action: PopularMoviesAction) =
        flow {
            emit(when(action) {
                PopularMoviesAction.Get,
                PopularMoviesAction.Refresh -> {
                    currentPage = 1
                    moviesRepo.getPopular(currentPage)
                }
                PopularMoviesAction.NextPage -> moviesRepo.getPopular(++currentPage)
            })
        }
            .map { response ->
                when(action) {
                    PopularMoviesAction.Get -> {
                        when (response) {
                            is RequestResult.Success -> PopularMoviesResult.Success(response.data)
                            is RequestResult.Error -> PopularMoviesResult.Error(response.exception)
                        }
                    }
                    PopularMoviesAction.NextPage -> {
                        when (response) {
                            is RequestResult.Success -> PopularMoviesResult.NextPageSuccess(response.data)
                            is RequestResult.Error -> PopularMoviesResult.NextPageError(response.exception)
                        }
                    }
                    PopularMoviesAction.Refresh -> {
                        when (response) {
                            is RequestResult.Success -> PopularMoviesResult.RefreshSuccess(response.data)
                            is RequestResult.Error -> PopularMoviesResult.RefreshError(response.exception)
                        }
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .onStart { emit(when(action) {
                PopularMoviesAction.Get -> PopularMoviesResult.Loading
                PopularMoviesAction.NextPage -> PopularMoviesResult.NextPageLoading
                PopularMoviesAction.Refresh -> PopularMoviesResult.RefreshLoading
            }) }

}