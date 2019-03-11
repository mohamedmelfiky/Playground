package com.example.mvisample.presentation.base

sealed class Event
data class ShowSnackBar(val text: String) : Event()
data class ShowToast(val text: String) : Event()