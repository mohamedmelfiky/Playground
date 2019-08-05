package com.example.domain.udf

import kotlinx.coroutines.flow.Flow

abstract class BaseUseCase<in A: BaseAction, out R: BaseResult> {
    abstract fun invoke(action: A): Flow<R>
}