package com.example.domain.udf

import kotlinx.coroutines.flow.Flow

abstract class BaseUseCase<A: BaseAction, R: BaseResult> {
    abstract fun invoke(action: A): Flow<R>
}