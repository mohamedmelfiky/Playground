package com.example.mvisample.mvibase

sealed class SingleEvent
data class ShowSnackBar(val text: String) : SingleEvent()
data class ShowToast(val text: String) : SingleEvent()