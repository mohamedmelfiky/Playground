package com.example.mvisample.mvibase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.udf.Machine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseViewModel<E: BaseUiEvent, S : BaseUiModel>(
    machine: Machine,
    initialState: S
): ViewModel() {

    private val _uiEventChannel = Channel<E>()
    private val actionsChannel = _uiEventChannel.consumeAsFlow()
        .onEach { event -> Timber.tag("ChannelsEvent").i(event.toString()) }
        .map(::eventToAction)
        .onEach { action -> Timber.tag("ChannelsAction").i(action.toString()) }
    private val _stateLiveData = MutableLiveData<S>()
    val stateLiveData: LiveData<S> = _stateLiveData

    init {
        machine.start(actionChannel = actionsChannel, scope = viewModelScope)
        machine.resultsReceiveChannel.consumeAsFlow()
            .distinctUntilChanged()
            .scan(initialState) { state, result -> resultToUiModel(state, result) }
            .onEach { state -> Timber.tag("ChannelsState").i(state.toString()) }
            .flowOn(Dispatchers.Default)
            .onEach { state -> _stateLiveData.value = state }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    fun sendEvent(uiEvent: E) = viewModelScope.launch {
        _uiEventChannel.send(uiEvent)
    }

    abstract suspend fun eventToAction(uiEvent: E): BaseAction
    abstract suspend fun resultToUiModel(state: S, result: BaseResult): S

}