package com.example.mvisample.data.remote

import com.example.mvisample.domain.entity.Result
import java.lang.Exception

suspend fun <T> safeApiCall(
    call: suspend () -> T
) : Result<T> {
    return try {
        Result.Success(call.invoke())
    } catch (e: Exception) {
        Result.Error(e)
    }
}