package com.example.mvisample.presentation.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment<A : BaseAction, R : BaseResult, S : BaseState> : Fragment() {

    protected val viewModel: BaseViewModel<A, R, S> by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stateLiveData.observe(this, Observer { renderState(it) })
        viewModel.singleEventLiveData.observe(this, Observer { onEvent(it) })
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