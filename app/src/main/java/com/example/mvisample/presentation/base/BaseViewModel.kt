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

    private val actionLiveData = MutableLiveData<A>()
    private val resultMutableLiveData = MutableLiveData<R>()

    private val stateMutableLiveData = MutableLiveData<S>(initialState)
    val stateLiveData: LiveData<S>
        get() = stateMutableLiveData.sideEffect { Timber.tag(LOG_TAG).d("State: $it") }

    private val singleEvent = SingleLiveEvent<Event>()
    val singleEventLiveData: LiveData<Event>
        get() = singleEvent

    private val actionObserver = Observer<A> { action ->
        action?.let {
            Timber.tag(LOG_TAG).d("Action: $action")
            actionToResult(action, resultMutableLiveData)
        }
    }
    private val resultObserver = Observer<R> { result ->
        result?.let {
            stateMutableLiveData.value?.let { previousState ->
                Timber.tag(LOG_TAG).d("Result: $result")
                reducer(previousState, result, stateMutableLiveData)
                sideEffect(result, singleEvent)
            }
        }
    }

    init {
        actionLiveData.observeForever(actionObserver)
        resultMutableLiveData.observeForever(resultObserver)
    }

    protected abstract fun actionToResult(action: A, resultLiveData: MutableLiveData<R>)
    protected abstract fun reducer(previousState: S, result: R, stateLiveData: MutableLiveData<S>)
    protected abstract fun sideEffect(result: R, eventLiveData: MutableLiveData<Event>)

    @MainThread
    fun sendAction(action: A) {
        actionLiveData.value = action
    }

    @MainThread
    protected fun sendSingleEvent(event: Event) {
        singleEvent.value = event
    }

    override fun onCleared() {
        actionLiveData.removeObserver(actionObserver)
        resultMutableLiveData.removeObserver(resultObserver)
        super.onCleared()
    }
}