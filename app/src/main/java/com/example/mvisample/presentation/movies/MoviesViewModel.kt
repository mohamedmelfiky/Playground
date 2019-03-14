package com.example.mvisample.presentation.movies

import android.view.View
import androidx.lifecycle.viewModelScope
import com.example.mvisample.domain.entity.Movie
import com.example.mvisample.domain.entity.Result
import com.example.mvisample.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.mvisample.presentation.base.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MoviesViewModel(
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase
) : BaseViewModel<MoviesState>(MoviesState()) {

    init {
        sendAction(Started)
    }

    override fun actOnAction(action: Action) {
        Timber.tag("Actions: ").i(action.toString())
        when (action) {
            Started -> {
                getMovies()
            }
            Refresh -> {
                refreshMovies()
            }
        }
    }

    private fun getMovies() {
        viewModelScope.launch(Dispatchers.Main) {
            sendState(MoviesState(loading = View.VISIBLE))
            val result = withContext(Dispatchers.IO) { getNowPlayingMoviesUseCase.get() }
            when (result) {
                is Result.Success -> {
                    sendState(
                        MoviesState(
                            loading = View.GONE,
                            mainView = View.VISIBLE,
                            movies = result.data
                        )
                    )
                }
                is Result.Error -> {
                    sendState(MoviesState(errorView = View.VISIBLE))
                }
            }
        }
    }

    private fun refreshMovies() {
        viewModelScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) { getNowPlayingMoviesUseCase.get() }
            when (result) {
                is Result.Success -> {
                    sendState(
                        MoviesState(
                            refreshing = false,
                            mainView = View.VISIBLE,
                            movies = result.data
                        )
                    )
                }
                is Result.Error -> {
                    sendState(MoviesState(errorView = View.VISIBLE))
                }
            }
        }
    }

}

data class MoviesState(
    val mainView: Int = View.GONE,
    val loading: Int = View.GONE,
    val refreshing: Boolean = false,
    val emptyView: Int = View.GONE,
    val errorView: Int = View.GONE,
    val errorText: String = "",
    val movies: List<Movie> = emptyList()
)