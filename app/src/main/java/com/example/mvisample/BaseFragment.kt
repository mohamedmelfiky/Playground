package com.example.mvisample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment<S, VM : BaseViewModel<S>>(modelClassName: Class<VM>) : Fragment() {

    protected val viewModel: VM by lazy { ViewModelProviders.of(this).get(modelClassName) }

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