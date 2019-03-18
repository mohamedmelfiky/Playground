package com.example.mvisample.presentation.movies

import android.os.Parcelable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.mvisample.presentation.base.*
import com.example.mvisample.util.exhaustive
import kotlinx.coroutines.*
import timber.log.Timber

abstract class MoviesViewModel : BaseViewModel<MoviesAction, MoviesResult, MoviesState>(
    MoviesState()
) {

    private var actionJob: Job? = null
    private var stateJob: Job? = null
    var layoutManagerState: Parcelable? = null
    private var currentPage = 1

    init {
        sendAction(Started)
    }

    abstract suspend fun getMovies(page: Int): RequestResult<List<Movie>>

    override fun actionToResult(action: MoviesAction, resultLiveData: MutableLiveData<MoviesResult>) {
        if (actionJob?.isActive == true) {
            actionJob?.cancel()
        }

        actionJob = when (action) {
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

    private fun startedAction(resultLiveData: MutableLiveData<MoviesResult>) = viewModelScope.launch {
        try {
            resultLiveData.value = MoviesResult.Loading
            val result = withContext(Dispatchers.IO) { getMovies(currentPage) }
            when (result) {
                is RequestResult.Success -> {
                    resultLiveData.value = MoviesResult.Success(result.data)
                }
                is RequestResult.Error -> {
                    resultLiveData.value = MoviesResult.Error(result.exception)
                }
            }
        } catch (e: CancellationException) {
            resultLiveData.value = MoviesResult.Cancelled
        }
    }

    private fun refreshAction(resultLiveData: MutableLiveData<MoviesResult>) = viewModelScope.launch {
        try {
            currentPage = 1
            resultLiveData.value = MoviesResult.RefreshLoading
            val result = withContext(Dispatchers.IO) { getMovies(currentPage) }
            when (result) {
                is RequestResult.Success -> {
                    resultLiveData.value = MoviesResult.RefreshSuccess(result.data)
                }
                is RequestResult.Error -> {
                    resultLiveData.value = MoviesResult.RefreshError(result.exception)
                }
            }
        } catch (e: CancellationException) {
            resultLiveData.value = MoviesResult.RefreshCancelled
        }
    }

    private fun loadMoreAction(page: Int, resultLiveData: MutableLiveData<MoviesResult>) = viewModelScope.launch {
        try {
            resultLiveData.value = MoviesResult.LoadMoreLoading
            val result = withContext(Dispatchers.IO) { getMovies(page) }
            when (result) {
                is RequestResult.Success -> {
                    resultLiveData.value = MoviesResult.LoadMoreSuccess(result.data)
                }
                is RequestResult.Error -> {
                    resultLiveData.value = MoviesResult.LoadMoreError(result.exception)
                }
            }
        } catch (e: CancellationException) {
            resultLiveData.value = MoviesResult.LoadMoreCancelled
        }
    }

    override fun reducer(
        previousState: MoviesState,
        result: MoviesResult,
        stateLiveData: MutableLiveData<MoviesState>
    ) {
        if (stateJob?.isActive == true) {
            stateJob?.cancel()
        }

        stateJob = viewModelScope.launch {
            when (result) {
                MoviesResult.Loading -> {
                    stateLiveData.value = previousState.copy(loadingVisibility = View.VISIBLE)
                }
                is MoviesResult.Success -> {
                    stateLiveData.value = previousState.copy(
                        loadingVisibility = View.GONE,
                        mainViewVisibility = View.VISIBLE,
                        movies = result.movies
                    )
                }
                is MoviesResult.Error -> {
                    stateLiveData.value =
                        previousState.copy(loadingVisibility = View.GONE, errorViewVisibility = View.VISIBLE)
                }
                MoviesResult.Cancelled -> {
                    stateLiveData.value = previousState.copy(loadingVisibility = View.GONE)
                }
                MoviesResult.RefreshLoading -> {
                    stateLiveData.value = previousState.copy(isRefreshing = true)
                }
                is MoviesResult.RefreshSuccess -> {
                    stateLiveData.value = previousState.copy(isRefreshing = false, movies = result.movies)
                }
                is MoviesResult.RefreshError -> {
                    stateLiveData.value = previousState.copy(isRefreshing = false)
                }
                MoviesResult.RefreshCancelled -> {
                    stateLiveData.value = previousState.copy(isRefreshing = false)
                }
                MoviesResult.LoadMoreLoading -> {
                    stateLiveData.value = previousState.copy(movies = previousState.movies)
                }
                is MoviesResult.LoadMoreSuccess -> {
                    try {
                        val movies = withContext(Dispatchers.Default) {
                            previousState.movies.plus(result.movies)
                        }
                        stateLiveData.value = previousState.copy(movies = movies)
                    } catch (e: CancellationException) {
                        stateLiveData.value = previousState.copy(movies = previousState.movies)
                    }
                }
                is MoviesResult.LoadMoreError -> {
                    stateLiveData.value = previousState.copy(movies = previousState.movies)
                }
                MoviesResult.LoadMoreCancelled -> {
                    stateLiveData.value = previousState.copy(movies = previousState.movies)
                }
            }.exhaustive
        }
    }

    override fun sideEffect(result: MoviesResult, eventLiveData: MutableLiveData<Event>) {
        when (result) {
            is MoviesResult.RefreshError -> {
                eventLiveData.value = ShowSnackBar("Something went wrong. Please try again.")
            }
            is MoviesResult.LoadMoreLoading -> {
                eventLiveData.value = ShowToast("Loading more data.")
            }
            is MoviesResult.LoadMoreError -> {
                eventLiveData.value = ShowToast("Load more error.")
            }
        }
    }

}