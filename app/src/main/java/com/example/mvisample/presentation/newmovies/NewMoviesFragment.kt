package com.example.mvisample.presentation.newmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mvisample.R
import com.example.mvisample.presentation.OnLoadMoreListener
import com.example.mvisample.presentation.base.Event
import com.example.mvisample.presentation.base.NewBaseFragment
import com.example.mvisample.presentation.base.ShowSnackBar
import com.example.mvisample.presentation.base.ShowToast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.movies_fragment.view.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*

class NewMoviesFragment : NewBaseFragment<MoviesAction, MoviesResult, MoviesState, NewMoviesViewModel>(
    NewMoviesViewModel::class.java
), SwipeRefreshLayout.OnRefreshListener {

    private val moviesAdapter = NewMoviesAdapter()
    private val onLoadMoreListener = OnLoadMoreListener { sendAction(LoadMore) }

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
        mainViewCl.visibility = state.mainView
        loadingPb.visibility = state.loading
        moviesSrl.isRefreshing = state.refreshing
        emptyView.visibility = state.emptyView
        errorView.visibility = state.errorView
        errorTv.text = state.errorText
        onLoadMoreListener.isLoading = state.isLoadingMore
        onLoadMoreListener.isLastPage = state.isLastPage
    }

    override fun onEvent(event: Event) {
        when(event) {
            is ShowSnackBar -> {
                Snackbar.make(rootCl, event.text, Snackbar.LENGTH_SHORT).show()
            }
            is ShowToast -> {
                Toast.makeText(requireContext(), event.text, Toast.LENGTH_SHORT).show()
            }
        }
    }

}