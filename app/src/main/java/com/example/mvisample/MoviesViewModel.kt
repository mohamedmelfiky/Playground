package com.example.mvisample

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MoviesViewModel : ViewModel() {

    private val moviesRepo = MoviesRepo(getApiService())

    val actionLiveData = MutableLiveData<MoviesAction>()
    val stateLiveData = MutableLiveData<MoviesState>()

    private val actionObserver = { action: MoviesAction -> onAction(action) }

    init {
        actionLiveData.observeForever(actionObserver)
        actionLiveData.value = MoviesAction.Started
    }

    private fun onAction(action: MoviesAction) {
        when(action) {
            MoviesAction.Started -> {
                getMovies()
            }
            MoviesAction.Refresh -> {
                refreshMovies()
            }
        }
    }

    private fun getMovies() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                stateLiveData.value = MoviesState(loading = View.VISIBLE)
                val result = withContext(Dispatchers.IO) { moviesRepo.getNowMovies() }
                stateLiveData.value = MoviesState(
                    loading = View.GONE,
                    mainView = View.VISIBLE,
                    movies = result.movies?.filterNotNull() ?: emptyList()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                stateLiveData.value = MoviesState(errorView = View.VISIBLE)
            }
        }
    }

    private fun refreshMovies() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) { moviesRepo.getNowMovies() }
                stateLiveData.value = MoviesState(
                    refreshing = false,
                    mainView = View.VISIBLE,
                    movies = result.movies?.filterNotNull() ?: emptyList()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                stateLiveData.value = MoviesState(errorView = View.VISIBLE)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        actionLiveData.removeObserver(actionObserver)
    }
}

sealed class MoviesAction {
    object Started : MoviesAction()
    object Refresh : MoviesAction()
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