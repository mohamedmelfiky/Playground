package com.example.mvisample.presentation.base

sealed class Action
object Started : Action()
object Refresh : Action()