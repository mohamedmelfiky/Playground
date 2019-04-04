package com.example.mvisample.presentation.movies

import android.os.Parcelable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.Movie
import com.example.domain.entity.MovieLoading
import com.example.domain.entity.RequestResult
import com.example.mvisample.presentation.base.*
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

    override fun actionToResult(action: MoviesAction, resultLiveData: MutableLiveData<MoviesResult>) {
        if (action is Started) {
            if (isFirstTime) {
                return
            } else {
                isFirstTime = true
            }
        }

        when (action) {
            Started -> {
                startedAction(resultLiveData)
            }
            Refresh -> {
                refreshAction(resultLiveData)
            }
            LoadMore -> {
                loadMoreAction(++currentPage, resultLiveData)
            }
        }
    }

    private fun startedAction(resultLiveData: MutableLiveData<MoviesResult>) {
        if (actionJob?.isActive == true) {
            return
        }

        actionJob = viewModelScope.launch {
            safeAction(
                action = {
                    resultLiveData.value = Loading
                    val result = withContext(Dispatchers.IO) { getMovies(currentPage) }
                    when (result) {
                        is RequestResult.Success -> {
                            resultLiveData.value = Success(result.data)
                        }
                        is RequestResult.Error -> {
                            resultLiveData.value = Error(result.exception)
                        }
                    }
                },
                onCancel = {
                    resultLiveData.value = Cancelled
                }
            )
        }
    }

    private fun refreshAction(resultLiveData: MutableLiveData<MoviesResult>) {
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
                    resultLiveData.value = RefreshLoading
                    val result = withContext(Dispatchers.IO) { getMovies(currentPage) }
                    when (result) {
                        is RequestResult.Success -> {
                            resultLiveData.value = RefreshSuccess(result.data)
                        }
                        is RequestResult.Error -> {
                            resultLiveData.value = RefreshError(result.exception)
                        }
                    }
                },
                onCancel = {
                    resultLiveData.value = RefreshCancelled
                }
            )
        }
    }

    private fun loadMoreAction(page: Int, resultLiveData: MutableLiveData<MoviesResult>) {
        if (loadMoreJob?.isActive == true) {
            return
        }

        if (refreshJob?.isActive == true) {
            refreshJob?.cancel()
        }

        loadMoreJob = viewModelScope.launch {
            safeAction(
                action = {
                    resultLiveData.value = LoadMoreLoading
                    val result = withContext(Dispatchers.IO) { getMovies(page) }
                    when (result) {
                        is RequestResult.Success -> {
                            resultLiveData.value = LoadMoreSuccess(result.data)
                        }
                        is RequestResult.Error -> {
                            resultLiveData.value = LoadMoreError(result.exception)
                        }
                    }
                },
                onCancel = {
                    resultLiveData.value = LoadMoreCancelled
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

    override fun sideEffect(result: MoviesResult): Event? {
        return when (result) {
            is RefreshError -> {
                ShowSnackBar("Something went wrong. Please try again.")
            }
            is LoadMoreLoading -> {
                ShowToast("Loading more data.")
            }
            is LoadMoreError -> {
                ShowToast("Load more error.")
            }
            else -> null
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