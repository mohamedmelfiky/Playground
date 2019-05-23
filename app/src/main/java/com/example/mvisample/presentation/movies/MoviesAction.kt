package com.example.mvisample.presentation.movies

import com.example.mvisample.mvibase.BaseAction

sealed class MoviesAction : BaseAction
object Started : MoviesAction()
object Refresh : MoviesAction()
object LoadMore : MoviesAction()