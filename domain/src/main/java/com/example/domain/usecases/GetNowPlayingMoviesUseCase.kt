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
class GetNowPlayingMoviesUseCase(
    private val moviesRepo: IMoviesRepo
) : BaseUseCase<GetNowPlayingMoviesUseCase.NowPlayingAction, GetNowPlayingMoviesUseCase.NowPlayingResult>() {

    sealed class NowPlayingAction : BaseAction {
        object Get: NowPlayingAction()
        object NextPage: NowPlayingAction()
        object Refresh: NowPlayingAction()
    }

    sealed class NowPlayingResult : BaseResult {
        object Loading : NowPlayingResult()
        data class Success(val movies: List<Movie>) : NowPlayingResult()
        data class Error(val exception: Exception) : NowPlayingResult()

        object RefreshLoading : NowPlayingResult()
        data class RefreshSuccess(val movies: List<Movie>) : NowPlayingResult()
        data class RefreshError(val error: Throwable) : NowPlayingResult()

        object NextPageLoading : NowPlayingResult()
        data class NextPageSuccess(val movies: List<Movie>) : NowPlayingResult()
        data class NextPageError(val error: Throwable) : NowPlayingResult()
    }

    private var currentPage: Int = 1

    override fun invoke(action: NowPlayingAction) =
        flow {
            emit(when(action) {
                NowPlayingAction.Get,
                NowPlayingAction.Refresh -> {
                    currentPage = 1
                    moviesRepo.getNowPlaying(currentPage)
                }
                NowPlayingAction.NextPage -> moviesRepo.getNowPlaying(++currentPage)
            })
        }
            .map { response ->
                when(action) {
                    NowPlayingAction.Get -> {
                        when (response) {
                            is RequestResult.Success -> NowPlayingResult.Success(response.data)
                            is RequestResult.Error -> NowPlayingResult.Error(response.exception)
                        }
                    }
                    NowPlayingAction.NextPage -> {
                        when (response) {
                            is RequestResult.Success -> NowPlayingResult.NextPageSuccess(response.data)
                            is RequestResult.Error -> NowPlayingResult.NextPageError(response.exception)
                        }
                    }
                    NowPlayingAction.Refresh -> {
                        when (response) {
                            is RequestResult.Success -> NowPlayingResult.RefreshSuccess(response.data)
                            is RequestResult.Error -> NowPlayingResult.RefreshError(response.exception)
                        }
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .onStart { emit(when(action) {
                NowPlayingAction.Get -> NowPlayingResult.Loading
                NowPlayingAction.NextPage -> NowPlayingResult.NextPageLoading
                NowPlayingAction.Refresh -> NowPlayingResult.RefreshLoading
            }) }

}