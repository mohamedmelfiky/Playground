package com.example.mvisample

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MoviesViewModel : BaseViewModel<MoviesState>(MoviesState()) {

    private val moviesRepo = MoviesRepo(getApiService())

    override fun actOnAction(action: Action) {
        when(action) {
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
            try {
                sendState(MoviesState(loading = View.VISIBLE))
                val result = withContext(Dispatchers.IO) { moviesRepo.getNowMovies() }
                sendState(MoviesState(
                    loading = View.GONE,
                    mainView = View.VISIBLE,
                    movies = result.movies?.filterNotNull() ?: emptyList()
                ))
            } catch (e: Exception) {
                e.printStackTrace()
                sendState(MoviesState(errorView = View.VISIBLE))
            }
        }
    }

    private fun refreshMovies() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) { moviesRepo.getNowMovies() }
                sendState(MoviesState(
                    refreshing = false,
                    mainView = View.VISIBLE,
                    movies = result.movies?.filterNotNull() ?: emptyList()
                ))
            } catch (e: Exception) {
                e.printStackTrace()
                sendState(MoviesState(errorView = View.VISIBLE))
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