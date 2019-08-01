package com.example.mvisample.presentation.movies

import android.os.Parcelable
import android.view.View
import com.example.domain.entity.MovieItem
import com.example.domain.udf.Machine
import com.example.mvisample.mvibase.BaseUiEvent
import com.example.mvisample.mvibase.BaseUiModel
import com.example.mvisample.mvibase.BaseViewModel
import kotlinx.coroutines.*

@FlowPreview
@ExperimentalCoroutinesApi
abstract class MoviesViewModel(
    machine: MoviesMachine
) : BaseViewModel<MoviesEvents, MoviesUiModel>(
    machine,
    MoviesUiModel()
) {

    var layoutManagerState: Parcelable? = null

    init {
        sendEvent(MoviesEvents.Started)
    }

}

sealed class MoviesEvents : BaseUiEvent {
    object Started : MoviesEvents()
    object Refresh : MoviesEvents()
    object LoadMore : MoviesEvents()
}

data class MoviesUiModel(
    val mainViewVisibility: Int = View.GONE,
    val loadingVisibility: Int = View.GONE,
    val isRefreshing: Boolean = false,
    val emptyViewVisibility: Int = View.GONE,
    val errorViewVisibility: Int = View.GONE,
    val errorText: String = "",
    val isLoadingMore: Boolean = false,
    val isLastPage: Boolean = false,
    val movies: List<MovieItem> = emptyList()
) : BaseUiModel