package com.example.mvisample.presentation.movies

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.domain.entity.Movie
import com.example.mvisample.R
import com.example.mvisample.mvibase.BaseFragment
import com.example.mvisample.presentation.common.OnLoadMoreListener
import com.example.mvisample.presentation.now_playing.NowPlayingFragmentDirections
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.movies_fragment.view.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
abstract class MoviesFragment :
    BaseFragment<MoviesEvents, MoviesUiModel>(
        R.layout.movies_fragment
    ),
    SwipeRefreshLayout.OnRefreshListener,
    OnMovieClickListener {

    abstract override val viewModel: MoviesViewModel
    private val adapter by lazy { MoviesAdapter(this) }
    private val onLoadMoreListener = OnLoadMoreListener(5) { viewModel.sendEvent(MoviesEvents.LoadMore) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.moviesRv.adapter = adapter
        view.moviesRv.setHasFixedSize(true)
        view.moviesRv.addOnScrollListener(onLoadMoreListener)
        view.moviesSrl.setOnRefreshListener(this)
        // We save the state of the layoutManger because onSaveInstance does not called when
        // Changing the fragment called only when rotation changed
        if (savedInstanceState == null) {
            view.moviesRv.layoutManager?.onRestoreInstanceState(viewModel.layoutManagerState)
        }
    }

    override fun onDestroyView() {
        moviesRv.removeOnScrollListener(onLoadMoreListener)
        viewModel.layoutManagerState = moviesRv.layoutManager?.onSaveInstanceState()
        super.onDestroyView()
    }

    override fun onRefresh() {
        viewModel.sendEvent(MoviesEvents.Refresh)
    }

    override fun renderState(state: MoviesUiModel) {
        adapter.submitList(state.movies)
        moviesSrl.visibility = state.mainViewVisibility
        loadingPb.visibility = state.loadingVisibility
        moviesSrl.isRefreshing = state.isRefreshing
        emptyView.visibility = state.emptyViewVisibility
        errorView.visibility = state.errorViewVisibility
        errorTv.text = state.errorText
        onLoadMoreListener.isLoading = state.isLoadingMore
        onLoadMoreListener.isLastPage = state.isLastPage
    }

}