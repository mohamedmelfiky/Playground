package com.example.mvisample.presentation.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

fun <T, S> LiveData<T>.scan(initialState: S, reducer: (previousState: S, result: T) -> S): LiveData<S> {
    var accumulatedState = initialState
    return MediatorLiveData<S>().apply {
        value = initialState
        addSource(this@scan) { emittedValue ->
            accumulatedState = reducer(accumulatedState, emittedValue!!)
            value = accumulatedState
        }
    }
}

/**
 * Maps any values that were emitted by the LiveData to the given function
 */
fun <T, O> LiveData<T>.map(function: (T) -> O): LiveData<O> {
    return Transformations.map(this, function)
}

/**
 * Maps any values that were emitted by the LiveData to the given function that produces another LiveData
 */
fun <T, O> LiveData<T>.switchMap(function: (T) -> LiveData<O>): LiveData<O> {
    return Transformations.switchMap(this, function)
}

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return Transformations.distinctUntilChanged(this)
}

fun <T> LiveData<T>.sideEffect(function: (T) -> Unit): LiveData<T> {
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) { value ->
        value?.let { function(it) }
        mutableLiveData.value = value
    }
    return mutableLiveData
}