package com.example.mvisample.mvibase

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<A : BaseAction, R : BaseResult, S : BaseState> : AppCompatActivity() {

    protected abstract val viewModel: BaseViewModel<A, R, S>
    private val rootView: View by lazy { window.decorView.findViewById<View>(android.R.id.content) }

    override fun onStart() {
        super.onStart()
        viewModel.observe(this, Observer { renderState(it) }, Observer { onEvent(it) })
    }

    abstract fun renderState(state: S)

    protected open fun onEvent(event: SingleEvent) {
        when (event) {
            is ShowSnackBar -> Snackbar.make(rootView, event.text, Snackbar.LENGTH_SHORT).show()
            is ShowToast -> Toast.makeText(this, event.text, Toast.LENGTH_SHORT).show()
        }
    }

    fun sendAction(action: A) {
        viewModel.sendAction(action)
    }
}