package com.example.mvisample.presentation.common

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

fun <T> LiveData<T>.sideEffect(function: (T) -> Unit): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) { value ->
        value?.let { function(it) }
        mutableLiveData.value = value
    }
    return mutableLiveData
}

fun <T> MutableLiveData<T>.sideEffect(function: (T) -> Unit): MutableLiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) { value ->
        value?.let { function(it) }
        mutableLiveData.value = value
    }
    return mutableLiveData
}