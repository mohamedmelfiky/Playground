package com.example.mvisample.presentation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

abstract class NewBaseFragment<A : BaseAction, R : BaseResult, S : BaseState, VM : NewBaseViewModel<A, R, S>>(modelClassName: Class<VM>) : Fragment() {

    protected val viewModel: VM by lazy { ViewModelProviders.of(this).get(modelClassName) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stateLiveData.observe(this, Observer { renderState(it) })
        viewModel.singleEventLiveData.observe(this, Observer { onEvent(it) })
    }

    abstract fun renderState(state: S)
    abstract fun onEvent(event: Event)

    fun sendAction(action: A) {
        viewModel.sendAction(action)
    }
}