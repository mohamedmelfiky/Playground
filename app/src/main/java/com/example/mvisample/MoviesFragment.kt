package com.example.mvisample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*

class MoviesFragment : BaseFragment<MoviesState, MoviesViewModel>(
    MoviesViewModel::class.java
), SwipeRefreshLayout.OnRefreshListener {

    private val moviesAdapter = MoviesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)

        moviesRv.adapter = moviesAdapter
        moviesSrl.setOnRefreshListener(this)

        return view
    }

    override fun renderState(
        state: MoviesState
    ) {
        moviesSrl.visibility = state.mainView
        loadingPb.visibility = state.loading
        moviesSrl.isRefreshing = state.refreshing
        emptyView.visibility = state.emptyView
        errorView.visibility = state.errorView
        errorTv.text = state.errorText
        moviesAdapter.submitList(state.movies)
    }

    override fun onEvent(
        event: Event
    ) {
        when(event) {
            is ShowSnackBar -> {
                Snackbar.make(rootCl, event.text, Snackbar.LENGTH_SHORT).show()
            }
            is ShowToast -> {
                Toast.makeText(requireContext(), event.text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRefresh() {
        sendAction(Refresh)
    }

}