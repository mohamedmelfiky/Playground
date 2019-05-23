package com.example.mvisample.presentation.movies

import android.view.View
import com.example.domain.entity.MovieItem
import com.example.mvisample.mvibase.BaseState

data class MoviesState(
    val mainViewVisibility: Int = View.GONE,
    val loadingVisibility: Int = View.GONE,
    val isRefreshing: Boolean = false,
    val emptyViewVisibility: Int = View.GONE,
    val errorViewVisibility: Int = View.GONE,
    val errorText: String = "",
    val isLoadingMore: Boolean = false,
    val isLastPage: Boolean = false,
    val movies: List<MovieItem> = emptyList()
) : BaseState