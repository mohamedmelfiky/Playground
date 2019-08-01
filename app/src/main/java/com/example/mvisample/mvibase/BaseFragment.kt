package com.example.mvisample.mvibase

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseFragment<E: BaseUiEvent, S : BaseUiModel>(
    @LayoutRes val contentLayoutId: Int
): Fragment(contentLayoutId) {

    abstract val viewModel: BaseViewModel<E, S>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stateLiveData.observe(this, Observer { state -> state?.let { renderState(it) } })
    }

    abstract fun renderState(state: S)

}