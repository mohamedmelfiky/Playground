package com.example.data.remote

import com.example.domain.entity.RequestResult
import java.lang.Exception

suspend fun <T> safeApiCall(
    call: suspend () -> T
) : RequestResult<T> {
    return try {
        RequestResult.Success(call.invoke())
    } catch (e: Exception) {
        RequestResult.Error(e)
    }
}