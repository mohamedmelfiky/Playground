package com.example.mvisample.presentation.newmovies

import com.example.mvisample.domain.entity.Movie
import com.example.mvisample.presentation.base.BaseResult

sealed class MoviesResult : BaseResult {
    object Loading : MoviesResult()
    object RefreshLoading : MoviesResult()
    object LoadMoreLoading : MoviesResult()
    data class Success(val movies: List<Movie>) : MoviesResult()
    data class RefreshSuccess(val movies: List<Movie>) : MoviesResult()
    data class LoadMoreSuccess(val movies: List<Movie>) : MoviesResult()
    data class Error(val error: Throwable) : MoviesResult()
    data class RefreshError(val error: Throwable) : MoviesResult()
    data class LoadMoreError(val error: Throwable) : MoviesResult()
}