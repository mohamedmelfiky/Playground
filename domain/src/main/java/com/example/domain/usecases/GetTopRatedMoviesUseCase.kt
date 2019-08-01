package com.example.domain.usecases

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.repos.IMoviesRepo
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.udf.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.lang.Exception

@ExperimentalCoroutinesApi
class GetTopRatedMoviesUseCase(
    private val moviesRepo: IMoviesRepo
): BaseUseCase<GetTopRatedMoviesUseCase.TopRatedAction, GetTopRatedMoviesUseCase.TopRatedResult>() {

    sealed class TopRatedAction : BaseAction {
        object Get: TopRatedAction()
        object NextPage: TopRatedAction()
        object Refresh: TopRatedAction()
    }

    sealed class TopRatedResult : BaseResult {
        object Loading : TopRatedResult()
        data class Success(val movies: List<Movie>) : TopRatedResult()
        data class Error(val exception: Exception) : TopRatedResult()

        object RefreshLoading : TopRatedResult()
        data class RefreshSuccess(val movies: List<Movie>) : TopRatedResult()
        data class RefreshError(val error: Throwable) : TopRatedResult()

        object NextPageLoading : TopRatedResult()
        data class NextPageSuccess(val movies: List<Movie>) : TopRatedResult()
        data class NextPageError(val error: Throwable) : TopRatedResult()
    }

    private var currentPage: Int = 1

    suspend fun get(page: Int = 1): RequestResult<List<Movie>> {
        return moviesRepo.getTopRated(page)
    }

    override fun invoke(action: TopRatedAction) =
        flow {
            emit(when(action) {
                TopRatedAction.Get,
                TopRatedAction.Refresh -> {
                    currentPage = 1
                    moviesRepo.getTopRated(currentPage)
                }
                TopRatedAction.NextPage -> moviesRepo.getTopRated(++currentPage)
            })
        }
            .map { response ->
                when(action) {
                    TopRatedAction.Get -> {
                        when (response) {
                            is RequestResult.Success -> TopRatedResult.Success(response.data)
                            is RequestResult.Error -> TopRatedResult.Error(response.exception)
                        }
                    }
                    TopRatedAction.NextPage -> {
                        when (response) {
                            is RequestResult.Success -> TopRatedResult.NextPageSuccess(response.data)
                            is RequestResult.Error -> TopRatedResult.NextPageError(response.exception)
                        }
                    }
                    TopRatedAction.Refresh -> {
                        when (response) {
                            is RequestResult.Success -> TopRatedResult.RefreshSuccess(response.data)
                            is RequestResult.Error -> TopRatedResult.RefreshError(response.exception)
                        }
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .onStart { emit(when(action) {
                TopRatedAction.Get -> TopRatedResult.Loading
                TopRatedAction.NextPage -> TopRatedResult.NextPageLoading
                TopRatedAction.Refresh -> TopRatedResult.RefreshLoading
            }) }
}