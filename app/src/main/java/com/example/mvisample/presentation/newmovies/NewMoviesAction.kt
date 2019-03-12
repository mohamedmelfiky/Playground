package com.example.mvisample.presentation.newmovies

import com.example.mvisample.presentation.base.BaseAction

sealed class MoviesAction : BaseAction
object Started : MoviesAction()
object Refresh : MoviesAction()
object LoadMore : MoviesAction()