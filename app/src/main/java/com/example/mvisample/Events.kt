package com.example.mvisample

sealed class Event
data class ShowSnackBar(val text: String) : Event()
data class ShowToast(val text: String) : Event()