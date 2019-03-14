package com.example.domain.repos

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult

interface IMoviesRepo {
    suspend fun getNowPlaying(page: Int) : RequestResult<List<Movie>>
}