package com.example.mvisample.presentation.base

import androidx.annotation.MainThread
import androidx.lifecycle.*

abstract class BaseViewModel<S>(initialState: S) : ViewModel() {

    private val actionLiveData = MutableLiveData<Action>()

    private val stateLiveData = MutableLiveData<S>(initialState)
    val state: LiveData<S>
        get() = stateLiveData

    private val singleEventLiveData = SingleLiveEvent<Event>()
    val singleEvent: LiveData<Event>
        get() = singleEventLiveData

    init {
        actionLiveData.observeForever(::onActionChanged)
    }

    private fun onActionChanged(action: Action?) {
        action?.let(::actOnAction)
    }

    protected abstract fun actOnAction(action: Action)

    @MainThread
    fun sendAction(action: Action) {
        actionLiveData.value = action
    }

    @MainThread
    protected fun sendState(state: S) {
        stateLiveData.value = state
    }

    @MainThread
    protected fun sendMessage(event: Event) {
        singleEventLiveData.value = event
    }

    override fun onCleared() {
        super.onCleared()
        actionLiveData.removeObserver(::onActionChanged)
    }
}