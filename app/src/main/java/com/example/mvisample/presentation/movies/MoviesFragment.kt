package com.example.mvisample.presentation.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mvisample.R
import com.example.mvisample.presentation.common.OnLoadMoreListener
import com.example.mvisample.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.movies_fragment.view.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*

class MoviesFragment : BaseFragment<MoviesAction, MoviesResult, MoviesState>(), SwipeRefreshLayout.OnRefreshListener {

    private val moviesAdapter = NewMoviesAdapter()
    private val onLoadMoreListener =
        OnLoadMoreListener { sendAction(LoadMore) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.movies_fragment, container, false)

        view.moviesRv.adapter = moviesAdapter
        view.moviesRv.setHasFixedSize(true)
        view.moviesRv.addOnScrollListener(onLoadMoreListener)
        view.moviesSrl.setOnRefreshListener(this)

        return view
    }

    override fun onDestroyView() {
        moviesRv.removeOnScrollListener(onLoadMoreListener)
        super.onDestroyView()
    }

    override fun onRefresh() {
        sendAction(Refresh)
    }

    override fun renderState(state: MoviesState) {
        moviesAdapter.submitList(state.movies)
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