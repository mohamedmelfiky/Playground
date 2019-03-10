package com.example.mvisample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel : MoviesViewModel by lazy { ViewModelProviders.of(this).get(MoviesViewModel::class.java) }
    private val moviesAdapter = MoviesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moviesRv.adapter = moviesAdapter
        moviesSrl.setOnRefreshListener(this)

        viewModel.stateLiveData.observe(this, Observer { render(it) })
    }

    private fun render(state: MoviesState) {
        moviesSrl.visibility = state.mainView
        loadingPb.visibility = state.loading
        moviesSrl.isRefreshing = state.refreshing
        emptyView.visibility = state.emptyView
        errorView.visibility = state.errorView
        errorTv.text = state.errorText
        moviesAdapter.submitList(state.movies)
    }

    override fun onRefresh() {
        viewModel.actionLiveData.value = MoviesAction.Refresh
    }
}