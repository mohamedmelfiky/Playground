package com.example.mvisample.presentation.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mvisample.R
import com.example.mvisample.presentation.common.OnLoadMoreListener
import com.example.mvisample.presentation.base.BaseFragment
import com.example.mvisample.presentation.common.AdapterOnInsertedListener
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.movies_fragment.view.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*
import timber.log.Timber

abstract class MoviesFragment<VM : MoviesViewModel> :
    BaseFragment<MoviesAction, MoviesResult, MoviesState, VM>(),
    SwipeRefreshLayout.OnRefreshListener,
    OnMovieClickListener {

    private val adapter by lazy { MoviesAdapter(this) }
    private val onLoadMoreListener = OnLoadMoreListener { sendAction(LoadMore) }
    private val adapterOnInsertedListener = AdapterOnInsertedListener(onLoadMoreListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.movies_fragment, container, false)

        adapter.registerAdapterDataObserver(adapterOnInsertedListener)
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
        adapter.unregisterAdapterDataObserver(adapterOnInsertedListener)
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
        onLoadMoreListener.isLastPage = state.isLastPage
    }

}