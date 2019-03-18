package com.example.mvisample.presentation.movies

import com.example.domain.entity.Movie
import com.example.mvisample.presentation.base.BaseResult

sealed class MoviesResult : BaseResult {
    object Loading : MoviesResult()
    data class Success(val movies: List<Movie>) : MoviesResult()
    data class Error(val error: Throwable) : MoviesResult()
    object Cancelled : MoviesResult()

    object RefreshLoading : MoviesResult()
    data class RefreshSuccess(val movies: List<Movie>) : MoviesResult()
    data class RefreshError(val error: Throwable) : MoviesResult()
    object RefreshCancelled : MoviesResult()

    object LoadMoreLoading : MoviesResult()
    data class LoadMoreSuccess(val movies: List<Movie>) : MoviesResult()
    data class LoadMoreError(val error: Throwable) : MoviesResult()
    object LoadMoreCancelled : MoviesResult()
}