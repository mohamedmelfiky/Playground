package com.example.mvisample

sealed class Action
object Started : Action()
object Refresh : Action()