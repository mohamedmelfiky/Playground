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
class GetUpcomingMoviesUseCase(
    private val moviesRepo: IMoviesRepo
) : BaseUseCase<GetUpcomingMoviesUseCase.UpcomingAction, GetUpcomingMoviesUseCase.UpcomingResult>() {

    sealed class UpcomingAction : BaseAction {
        object Get: UpcomingAction()
        object NextPage: UpcomingAction()
        object Refresh: UpcomingAction()
    }

    sealed class UpcomingResult : BaseResult {
        object Loading : UpcomingResult()
        data class Success(val movies: List<Movie>) : UpcomingResult()
        data class Error(val exception: Exception) : UpcomingResult()

        object RefreshLoading : UpcomingResult()
        data class RefreshSuccess(val movies: List<Movie>) : UpcomingResult()
        data class RefreshError(val error: Throwable) : UpcomingResult()

        object NextPageLoading : UpcomingResult()
        data class NextPageSuccess(val movies: List<Movie>) : UpcomingResult()
        data class NextPageError(val error: Throwable) : UpcomingResult()
    }

    private var currentPage: Int = 1

    override fun invoke(action: UpcomingAction) =
        flow {
            emit(when(action) {
                UpcomingAction.Get,
                UpcomingAction.Refresh -> {
                    currentPage = 1
                    moviesRepo.getUpcoming(currentPage)
                }
                UpcomingAction.NextPage -> moviesRepo.getUpcoming(++currentPage)
            })
        }
            .map { response ->
                when(action) {
                    UpcomingAction.Get -> {
                        when (response) {
                            is RequestResult.Success -> UpcomingResult.Success(response.data)
                            is RequestResult.Error -> UpcomingResult.Error(response.exception)
                        }
                    }
                    UpcomingAction.NextPage -> {
                        when (response) {
                            is RequestResult.Success -> UpcomingResult.NextPageSuccess(response.data)
                            is RequestResult.Error -> UpcomingResult.NextPageError(response.exception)
                        }
                    }
                    UpcomingAction.Refresh -> {
                        when (response) {
                            is RequestResult.Success -> UpcomingResult.RefreshSuccess(response.data)
                            is RequestResult.Error -> UpcomingResult.RefreshError(response.exception)
                        }
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .onStart { emit(when(action) {
                UpcomingAction.Get -> UpcomingResult.Loading
                UpcomingAction.NextPage -> UpcomingResult.NextPageLoading
                UpcomingAction.Refresh -> UpcomingResult.RefreshLoading
            }) }

}