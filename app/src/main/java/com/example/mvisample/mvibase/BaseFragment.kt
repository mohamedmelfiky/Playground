package com.example.mvisample.mvibase

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<A : BaseAction, R : BaseResult, S : BaseState> : Fragment() {

    protected abstract val viewModel: BaseViewModel<A, R, S>
    protected lateinit var state: S

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.resources = resources
        viewModel.observe(
            this,
            Observer {
                state = it
                renderState(it)
            }
            , Observer { onEvent(it) }
        )
    }

    abstract fun renderState(state: S)

    protected open fun onEvent(event: SingleEvent) {
        when (event) {
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