package com.example.mvisample.presentation.base

import com.example.domain.entity.Movie

//interface Result
//
//sealed class MoviesResult : Result {
//    object Loading : MoviesResult()
//    object RefreshLoading : MoviesResult()
//    object LoadMoreLoading : MoviesResult()
//    data class Succes(val movies: List<Movie>) : MoviesResult()
//    data class LoadMoreSucces(val movies: List<Movie>) : MoviesResult()
//    data class Error(val error: Throwable) : MoviesResult()
//    data class LoadMoreError(val error: Throwable) : MoviesResult()
//}