package com.example.mvisample.mvibase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.udf.Logger
import com.example.domain.udf.Machine
import com.example.mvisample.util.AndroidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseViewModel<E: BaseUiEvent, S : BaseUiModel>(
    machine: Machine,
    initialState: S,
    private val logger: Logger = AndroidLogger()
): ViewModel() {

    private val _uiEventChannel = Channel<E>()
    private val actionsChannel = _uiEventChannel.consumeAsFlow()
        .onEach { event -> logger.log("Event  -> $event") }
        .map(::eventToAction)
        .onEach { action -> logger.log("Action -> $action") }
    private val _stateLiveData = MutableLiveData<S>()
    val stateLiveData: LiveData<S> = _stateLiveData

    init {
        machine.start(actionChannel = actionsChannel, scope = viewModelScope)
        machine.resultsReceiveChannel
            .consumeAsFlow()
            .distinctUntilChanged()
            .scan(initialState) { state, result -> resultToUiModel(state, result) }
            .onEach { state -> logger.log("State  -> $state") }
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