package com.example.domain.udf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
abstract class Machine(
    private val type: FlowType = FlowType.SWITCH,
    private val logger: Logger
) {

    enum class FlowType {
        SWITCH,
        SUSPEND,
        PARALLEL
    }

    private val _resultsChannel = Channel<BaseResult>(Channel.CONFLATED)
    val resultsReceiveChannel: ReceiveChannel<BaseResult> = _resultsChannel

    fun start(
        actionChannel: Flow<BaseAction>,
        scope: CoroutineScope
    ) {
        when (type) {
            FlowType.SWITCH -> switch(actionChannel, scope)
            FlowType.SUSPEND -> suspend(actionChannel, scope)
            FlowType.PARALLEL -> parallel(actionChannel, scope)
        }
    }

    private fun switch(
        actionChannel: Flow<BaseAction>,
        scope: CoroutineScope
    ) {
        actionChannel
            .switchMap(::actOnAction)
            .onEach { result -> _resultsChannel.send(result) }
            .onEach { result -> logger.log("Result -> $result") }
            .catch { }
            .flowOn(Dispatchers.Default)
            .launchIn(scope)
    }

    private fun suspend(
        actionChannel: Flow<BaseAction>,
        scope: CoroutineScope
    ) {
        actionChannel
            .flatMapConcat(::actOnAction)
            .onEach { result -> _resultsChannel.send(result) }
            .onEach { result -> logger.log("Result -> $result") }
            .catch { }
            .flowOn(Dispatchers.Default)
            .launchIn(scope)
    }

    private fun parallel(
        actionChannel: Flow<BaseAction>,
        scope: CoroutineScope
    ) {
        actionChannel
            .onEach { action ->
                actOnAction(action)
                    .onEach { result -> _resultsChannel.send(result) }
                    .onEach { result -> logger.log("Result -> $result") }
                    .flowOn(Dispatchers.Default)
                    .launchIn(scope)
            }
            .catch { }
            .flowOn(Dispatchers.Default)
            .launchIn(scope)
    }

    abstract suspend fun actOnAction(action: BaseAction): Flow<BaseResult>

}