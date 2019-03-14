package com.example.mvisample.presentation.movies

import android.view.View
import com.example.mvisample.domain.entity.Movie
import com.example.mvisample.presentation.base.BaseState

data class MoviesState(
    val mainView: Int = View.GONE,
    val loading: Int = View.GONE,
    val refreshing: Boolean = false,
    val emptyView: Int = View.GONE,
    val errorView: Int = View.GONE,
    val errorText: String = "",
    val isLoadingMore: Boolean = false,
    val isLastPage: Boolean = false,
    val movies: List<Movie> = emptyList()
) : BaseState