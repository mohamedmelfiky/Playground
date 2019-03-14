package com.example.mvisample.presentation.movies

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvisample.domain.entity.Result
import com.example.mvisample.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.mvisample.presentation.base.BaseViewModel
import com.example.mvisample.presentation.base.ShowSnackBar
import com.example.mvisample.presentation.base.ShowToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesViewModel(
    private val getNowPlayingMoviesUseCase : GetNowPlayingMoviesUseCase
) : BaseViewModel<MoviesAction, MoviesResult, MoviesState>(
    MoviesState()
) {

    private var currentPage = 1

    init {
        sendAction(Started)
    }

    override fun actionToResult(action: MoviesAction): LiveData<MoviesResult> {
        val resultLiveData = MutableLiveData<MoviesResult>()

        when (action) {
            Started -> {
                viewModelScope.launch(Dispatchers.Main) {
                    resultLiveData.value = MoviesResult.Loading
                    val result = withContext(Dispatchers.IO) { getNowPlayingMoviesUseCase.get() }
                    when (result) {
                        is Result.Success -> {
                            resultLiveData.value =
                                MoviesResult.Success(result.data)
                        }
                        is Result.Error -> {
                            resultLiveData.value =
                                MoviesResult.Error(result.exception)
                        }
                    }
                }
            }
            Refresh -> {
                viewModelScope.launch(Dispatchers.Main) {
                    currentPage = 1
                    resultLiveData.value = MoviesResult.RefreshLoading
                    val result = withContext(Dispatchers.IO) { getNowPlayingMoviesUseCase.get() }
                    when (result) {
                        is Result.Success -> {
                            resultLiveData.value =
                                MoviesResult.RefreshSuccess(result.data)
                        }
                        is Result.Error -> {
                            resultLiveData.value =
                                MoviesResult.RefreshError(result.exception)
                        }
                    }
                }
            }
            LoadMore -> {
                viewModelScope.launch(Dispatchers.Main) {
                    resultLiveData.value = MoviesResult.LoadMoreLoading
                    val result = withContext(Dispatchers.IO) { getNowPlayingMoviesUseCase.get(++currentPage) }
                    when (result) {
                        is Result.Success -> {
                            resultLiveData.value =
                                MoviesResult.LoadMoreSuccess(result.data)
                        }
                        is Result.Error -> {
                            resultLiveData.value =
                                MoviesResult.LoadMoreError(result.exception)
                        }
                    }
                }
            }
        }

        return resultLiveData
    }

    override fun reducer(previousState: MoviesState, result: MoviesResult): MoviesState {
        return when (result) {
            MoviesResult.Loading -> {
                previousState.copy(loading = View.VISIBLE)
            }
            MoviesResult.RefreshLoading -> {
                previousState.copy(refreshing = true)
            }
            MoviesResult.LoadMoreLoading -> {
                previousState.copy(isLoadingMore = true)
            }
            is MoviesResult.Success -> {
                previousState.copy(
                    loading = View.GONE,
                    mainView = View.VISIBLE,
                    movies = result.movies
                )
            }
            is MoviesResult.RefreshSuccess -> {
                previousState.copy(refreshing = false, movies = result.movies)
            }
            is MoviesResult.LoadMoreSuccess -> {
                previousState.copy(isLoadingMore = false, movies = previousState.movies.plus(result.movies))
            }
            is MoviesResult.Error -> {
                previousState.copy(loading = View.GONE, errorView = View.VISIBLE)
            }
            is MoviesResult.LoadMoreError -> {
                previousState.copy(isLoadingMore = false)
            }
            is MoviesResult.RefreshError -> {
                previousState.copy(refreshing = false)
            }
        }
    }

    override fun sideEffect(result: MoviesResult) {
        when(result) {
            is MoviesResult.RefreshError -> {
                sendSingleEvent(ShowSnackBar("Something went wrong. Please try again."))
            }
            is MoviesResult.LoadMoreLoading -> {
                sendSingleEvent(ShowToast("Loading more data."))
            }
            is MoviesResult.LoadMoreError -> {
                sendSingleEvent(ShowToast("Load more error."))
            }
        }
    }

}