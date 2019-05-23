package com.example.mvisample.presentation.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mvisample.R
import com.example.mvisample.mvibase.BaseFragment
import com.example.mvisample.mvibase.BaseViewModel
import com.example.mvisample.presentation.common.OnLoadMoreListener
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.movies_fragment.view.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*
import timber.log.Timber

abstract class MoviesFragment :
    BaseFragment<MoviesAction, MoviesResult, MoviesState>(),
    SwipeRefreshLayout.OnRefreshListener,
    OnMovieClickListener {

    abstract override val viewModel: MoviesViewModel
    private val adapter by lazy { MoviesAdapter(this) }
    private val onLoadMoreListener = OnLoadMoreListener(5) { sendAction(LoadMore) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.movies_fragment, container, false)

        view.moviesRv.adapter = adapter
        view.moviesRv.setHasFixedSize(true)
        view.moviesRv.addOnScrollListener(onLoadMoreListener)
        view.moviesSrl.setOnRefreshListener(this)
        // We save the state of the layoutManger because onSaveInstance does not called when
        // Changing the fragment called only when rotation changed
        if (savedInstanceState == null) {
            view.moviesRv.layoutManager?.onRestoreInstanceState(viewModel.layoutManagerState)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendAction(Started)
    }

    override fun onDestroyView() {
        moviesRv.removeOnScrollListener(onLoadMoreListener)
        viewModel.layoutManagerState = moviesRv.layoutManager?.onSaveInstanceState()
        super.onDestroyView()
    }

    override fun onRefresh() {
        sendAction(Refresh)
    }

    override fun renderState(state: MoviesState) {
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