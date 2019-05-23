package com.example.mvisample.mvibase

import android.content.res.Resources
import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import timber.log.Timber

private const val LOG_TAG = "MviState"

abstract class BaseViewModel<A : BaseAction, R : BaseResult, S : BaseState>(initialState: S) : ViewModel() {

    lateinit var resources: Resources
    private val _resultMutableLiveData = MutableLiveData<R>()
    private val resultMutableLiveData = Transformations.distinctUntilChanged(_resultMutableLiveData)
    private val stateLiveData = MutableLiveData<S>(initialState)
    private val singleEventLiveData = SingleLiveEvent<SingleEvent>()

    private val resultObserver = Observer<R> { result: R ->
        Timber.tag(LOG_TAG).d("Result: $result")
        val newState = reducer(stateLiveData.value!!, result)
        Timber.tag(LOG_TAG).d("State: $newState")
        stateLiveData.value = newState
        resultToEvent(result)
    }

    init {
        Timber.tag(LOG_TAG).d("InitialState: $initialState")
    }

    fun observe(owner: LifecycleOwner, stateObserver: Observer<S>, eventObserver: Observer<SingleEvent>) {
        if (!resultMutableLiveData.hasObservers()) {
            resultMutableLiveData.observeForever(resultObserver)
        }
        Transformations.distinctUntilChanged(stateLiveData).observe(owner, stateObserver)
        singleEventLiveData.observe(owner, eventObserver)
    }

    /*
     * Runs in coroutine on main thread
     */
    protected abstract suspend fun actionToResult(action: A)

    @MainThread
    protected abstract fun reducer(previousState: S, result: R): S

    @MainThread
    protected abstract fun resultToEvent(result: R)

    fun sendAction(action: A) {
        Timber.tag(LOG_TAG).d("Action: $action")
        viewModelScope.launch {
            actionToResult(action)
        }
    }

    @MainThread
    protected fun setResult(result: R) {
        _resultMutableLiveData.value = result
    }

    @MainThread
    protected fun setSingleEvent(event: SingleEvent) {
        Timber.tag(LOG_TAG).d("SingleEvent: $event")
        singleEventLiveData.value = event
    }

    override fun onCleared() {
        resultMutableLiveData.removeObserver(resultObserver)
        super.onCleared()
    }
}