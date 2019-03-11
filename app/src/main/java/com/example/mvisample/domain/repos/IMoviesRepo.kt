package com.example.mvisample.domain.repos

import com.example.domain.entity.Movie
import com.example.mvisample.domain.entity.Result

interface IMoviesRepo {
    suspend fun getNowPlaying(page: Int) : Result<List<Movie>>
}