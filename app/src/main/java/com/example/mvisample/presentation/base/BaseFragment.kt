package com.example.mvisample.presentation.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<A : BaseAction, R : BaseResult, S : BaseState, VM : BaseViewModel<A, R, S>> : Fragment() {

    protected abstract  val viewModel: VM

    private val stateObservable = Observer<S> { renderState(it) }
    private val singleEventObservable = Observer<Event> { onEvent(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stateLiveData.observe(this, stateObservable)
        viewModel.singleEventLiveData.observe(this, singleEventObservable)
    }

    abstract fun renderState(state: S)

    protected open fun onEvent(event: Event) {
        when(event) {
            is ShowSnackBar -> {
                view?.let {
                    Snackbar.make(it, event.text, Snackbar.LENGTH_SHORT).show()
                }
            }
            is ShowToast -> {
                Toast.makeText(requireContext(), event.text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendAction(action: A) {
        viewModel.sendAction(action)
    }
}