package com.example.mvisample.presentation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment<S> : Fragment() {

    protected val viewModel: BaseViewModel<S> by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(this, Observer { renderState(it) })
        viewModel.singleEvent.observe(this, Observer { onEvent(it) })
    }

    abstract fun renderState(state: S)
    abstract fun onEvent(event: Event)

    fun sendAction(action: Action) {
        viewModel.sendAction(action)
    }
}