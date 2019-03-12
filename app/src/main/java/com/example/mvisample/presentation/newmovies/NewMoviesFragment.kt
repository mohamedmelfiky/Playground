package com.example.mvisample.presentation.newmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mvisample.R
import com.example.mvisample.presentation.base.Event
import com.example.mvisample.presentation.base.NewBaseFragment
import com.example.mvisample.presentation.base.ShowSnackBar
import com.example.mvisample.presentation.base.ShowToast
import com.example.mvisample.presentation.movies.MoviesAdapter
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.movies_fragment.view.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*

class NewMoviesFragment : NewBaseFragment<MoviesAction, MoviesResult, MoviesState, NewMoviesViewModel>(
    NewMoviesViewModel::class.java
), SwipeRefreshLayout.OnRefreshListener {

    private val moviesAdapter = NewMoviesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.movies_fragment, container, false)

        view.moviesRv.adapter = moviesAdapter
        view.moviesRv.setHasFixedSize(true)
        view.moviesSrl.setOnRefreshListener(this)
        view.moviesRv.scrollEvents().subscribe()

        return view
    }

    override fun onRefresh() {
        sendAction(Refresh)
    }

    override fun renderState(state: MoviesState) {
        moviesSrl.visibility = state.mainView
        loadingPb.visibility = state.loading
        moviesSrl.isRefreshing = state.refreshing
        emptyView.visibility = state.emptyView
        errorView.visibility = state.errorView
        errorTv.text = state.errorText
        moviesAdapter.submitList(state.movies)
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