package com.example.domain.entity

sealed class RequestResult<T> {
    data class Success<T>(val data: T): RequestResult<T>()
    data class Error<T>(val exception: Exception): RequestResult<T>()
}