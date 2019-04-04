package com.example.domain.entity


sealed class MovieItem

object MovieLoading : MovieItem()

data class Movie(
    val id: Int,
    val title: String,
    val poster: String
) : MovieItem()