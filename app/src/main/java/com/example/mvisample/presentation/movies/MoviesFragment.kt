package com.example.mvisample.presentation.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mvisample.R
import com.example.mvisample.presentation.base.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.movies_fragment.view.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*

class MoviesFragment : BaseFragment<MoviesState>(), SwipeRefreshLayout.OnRefreshListener {

    private val moviesAdapter = MoviesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.movies_fragment, container, false)

        view.moviesRv.adapter = moviesAdapter
        view.moviesRv.setHasFixedSize(true)
        view.moviesSrl.setOnRefreshListener(this)

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