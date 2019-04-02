package com.example.mvisample.presentation.base

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.example.mvisample.presentation.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val LOG_TAG = "MviState"

abstract class BaseViewModel<A : BaseAction, R : BaseResult, S : BaseState>(initialState: S) : ViewModel() {

    private val resultMutableLiveData = MutableLiveData<R>()

    val stateLiveData = resultMutableLiveData
        .sideEffect { Timber.tag(LOG_TAG).d("Result: $it") }
        .sideEffect { sideEffect(it)?.let { event -> singleEvent.value = event } }
        .scan(initialState) { previousState, result -> reducer(previousState, result) }
        .sideEffect { Timber.tag(LOG_TAG).d("State: $it") }

    private val singleEvent = SingleLiveEvent<Event>()
    val singleEventLiveData: LiveData<Event>
        get() = singleEvent

    private fun onAction(action: A) {
        Timber.tag(LOG_TAG).d("Action: $action")
        actionToResult(action, resultMutableLiveData)
    }

    protected abstract fun actionToResult(action: A, resultLiveData: MutableLiveData<R>)
    protected abstract fun reducer(previousState: S, result: R) : S
    protected abstract fun sideEffect(result: R) : Event?

    @MainThread
    fun sendAction(action: A) {
        onAction(action)
    }

    @MainThread
    protected fun sendSingleEvent(event: Event) {
        singleEvent.value = event
    }
}