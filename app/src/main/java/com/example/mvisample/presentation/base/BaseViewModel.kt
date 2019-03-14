package com.example.mvisample.presentation.base

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvisample.presentation.common.SingleLiveEvent
import com.example.mvisample.presentation.common.distinctUntilChanged
import com.example.mvisample.presentation.common.scan
import com.example.mvisample.presentation.common.sideEffect
import com.example.mvisample.presentation.common.switchMap
import timber.log.Timber

abstract class BaseViewModel<A : BaseAction, R : BaseResult, S : BaseState>(initialState: S) : ViewModel() {

    private val LOG_TAG = "MviState"
    private val actionLiveData = MutableLiveData<A>()

    private val singleEvent = SingleLiveEvent<Event>()
    val singleEventLiveData: LiveData<Event>
        get() = singleEvent

    val stateLiveData = actionLiveData
        .sideEffect { Timber.tag(LOG_TAG).d("Action: $it") }
        .switchMap(::actionToResult)
        .sideEffect { Timber.tag(LOG_TAG).d("RequestResult: $it") }
        .sideEffect(::sideEffect)
        .scan(initialState, ::reducer)
        .sideEffect { Timber.tag(LOG_TAG).d("State: $it") }
        .distinctUntilChanged()

    protected abstract fun actionToResult(action: A): LiveData<R>
    protected abstract fun reducer(previousState: S, result: R): S
    protected abstract fun sideEffect(result: R)

    @MainThread
    fun sendAction(action: A) {
        actionLiveData.value = action
    }

    @MainThread
    protected fun sendSingleEvent(event: Event) {
        singleEvent.value = event
    }
}