package com.example.mvisample.presentation.movies

import android.os.Parcelable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.Movie
import com.example.domain.entity.MovieLoading
import com.example.domain.entity.RequestResult
import com.example.mvisample.mvibase.BaseViewModel
import com.example.mvisample.mvibase.ShowSnackBar
import com.example.mvisample.mvibase.ShowToast
import kotlinx.coroutines.*

abstract class MoviesViewModel : BaseViewModel<MoviesAction, MoviesResult, MoviesState>(
    MoviesState()
) {

    private var actionJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null
    var layoutManagerState: Parcelable? = null
    private var currentPage = 1
    private var isFirstTime = false

    abstract suspend fun getMovies(page: Int): RequestResult<List<Movie>>

    override suspend fun actionToResult(action: MoviesAction) {
        if (action is Started) {
            if (isFirstTime) {
                return
            } else {
                isFirstTime = true
            }
        }

        when (action) {
            Started -> {
                startedAction()
            }
            Refresh -> {
                refreshAction()
            }
            LoadMore -> {
                loadMoreAction(++currentPage)
            }
        }
    }

    private fun startedAction() {
        if (actionJob?.isActive == true) {
            return
        }

        actionJob = viewModelScope.launch {
            safeAction(
                action = {
                    setResult(Loading)
                    val result = withContext(Dispatchers.IO) { getMovies(currentPage) }
                    when (result) {
                        is RequestResult.Success -> {
                            setResult(Success(result.data))
                        }
                        is RequestResult.Error -> {
                            setResult(Error(result.exception))
                        }
                    }
                },
                onCancel = {
                    setResult(Cancelled)
                }
            )
        }
    }

    private fun refreshAction() {
        if (refreshJob?.isActive == true) {
            return
        }

        if (loadMoreJob?.isActive == true) {
            loadMoreJob?.cancel()
        }

        refreshJob = viewModelScope.launch {
            safeAction(
                action = {
                    currentPage = 1
                    setResult(RefreshLoading)
                    val result = withContext(Dispatchers.IO) { getMovies(currentPage) }
                    when (result) {
                        is RequestResult.Success -> {
                            setResult(RefreshSuccess(result.data))
                        }
                        is RequestResult.Error -> {
                            setResult(RefreshError(result.exception))
                        }
                    }
                },
                onCancel = {
                    setResult(RefreshCancelled)
                }
            )
        }
    }

    private fun loadMoreAction(page: Int) {
        if (loadMoreJob?.isActive == true) {
            return
        }

        if (refreshJob?.isActive == true) {
            refreshJob?.cancel()
        }

        loadMoreJob = viewModelScope.launch {
            safeAction(
                action = {
                    setResult(LoadMoreLoading)
                    val result = withContext(Dispatchers.IO) { getMovies(page) }
                    when (result) {
                        is RequestResult.Success -> {
                            setResult(LoadMoreSuccess(result.data))
                        }
                        is RequestResult.Error -> {
                            setResult(LoadMoreError(result.exception))
                        }
                    }
                },
                onCancel = {
                    setResult(LoadMoreCancelled)
                }
            )
        }
    }

    override fun reducer(
        previousState: MoviesState,
        result: MoviesResult
    ): MoviesState {
        return when (result) {
            Loading -> {
                previousState.copy(loadingVisibility = View.VISIBLE)
            }
            is Success -> {
                previousState.copy(
                    loadingVisibility = View.GONE,
                    mainViewVisibility = View.VISIBLE,
                    movies = result.movies
                )
            }
            is Error -> {
                previousState.copy(loadingVisibility = View.GONE, errorViewVisibility = View.VISIBLE)
            }
            Cancelled -> {
                previousState.copy(loadingVisibility = View.GONE)
            }
            RefreshLoading -> {
                previousState.copy(isRefreshing = true)
            }
            is RefreshSuccess -> {
                previousState.copy(isRefreshing = false, movies = result.movies)
            }
            is RefreshError -> {
                previousState.copy(isRefreshing = false)
            }
            RefreshCancelled -> {
                previousState.copy(isRefreshing = false)
            }
            LoadMoreLoading -> {
                previousState.copy(isLoadingMore = true, movies = previousState.movies.plus(MovieLoading))
            }
            is LoadMoreSuccess -> {
                val movies = previousState.movies.dropLast(1).plus(result.movies)
                previousState.copy(isLoadingMore = false, movies = movies)
            }
            is LoadMoreError -> {
                previousState.copy(isLoadingMore = false, movies = previousState.movies.dropLast(1))
            }
            LoadMoreCancelled -> {
                previousState.copy(isRefreshing = true, isLoadingMore = false, movies = previousState.movies.dropLast(1))
            }
        }
    }

    override fun resultToEvent(result: MoviesResult) {
        when (result) {
            is RefreshError -> {
                setSingleEvent(ShowSnackBar("Something went wrong. Please try again."))
            }
            is LoadMoreLoading -> {
                setSingleEvent(ShowToast("Loading more data."))
            }
            is LoadMoreError -> {
                setSingleEvent(ShowToast("Load more error."))
            }
        }
    }

}

suspend fun safeAction(
    action: suspend () -> Unit,
    onCancel: suspend () -> Unit
) {
    try {
        action()
    } catch (e: CancellationException) {
        onCancel()
    }
}