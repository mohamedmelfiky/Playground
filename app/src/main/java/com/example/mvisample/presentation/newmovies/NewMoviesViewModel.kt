package com.example.mvisample.presentation.newmovies

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvisample.data.remote.BASE_URL
import com.example.mvisample.data.remote.getApiService
import com.example.mvisample.data.remote.getHttpClient
import com.example.mvisample.data.repos.MoviesRepo
import com.example.mvisample.domain.entity.Result
import com.example.mvisample.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.mvisample.presentation.base.NewBaseViewModel
import com.example.mvisample.presentation.base.ShowSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewMoviesViewModel : NewBaseViewModel<MoviesAction, MoviesResult, MoviesState>(MoviesState()) {

    // Should be injected with DI
    private val moviesRepo = MoviesRepo.getInstance(getApiService(BASE_URL, getHttpClient()))
    private val getNowPlayingMoviesUseCase = GetNowPlayingMoviesUseCase(moviesRepo)

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
                            resultLiveData.value = MoviesResult.Success(result.data)
                        }
                        is Result.Error -> {
                            resultLiveData.value = MoviesResult.Error(result.exception)
                        }
                    }
                }
            }
            Refresh -> {
                viewModelScope.launch(Dispatchers.Main) {
                    resultLiveData.value = MoviesResult.RefreshLoading
                    val result = withContext(Dispatchers.IO) { getNowPlayingMoviesUseCase.get() }
                    when (result) {
                        is Result.Success -> {
                            resultLiveData.value = MoviesResult.RefreshSuccess(result.data)
                        }
                        is Result.Error -> {
                            resultLiveData.value = MoviesResult.RefreshError(result.exception)
                        }
                    }
                }
            }
            LoadMore -> {

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
                previousState.copy()
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
                previousState.copy()
            }
            is MoviesResult.Error -> {
                previousState.copy(loading = View.GONE, errorView = View.VISIBLE)
            }
            is MoviesResult.LoadMoreError -> {
                previousState.copy()
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
        }
    }

}