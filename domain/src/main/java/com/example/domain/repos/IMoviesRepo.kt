package com.example.domain.repos

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult

interface IMoviesRepo {
    suspend fun getPopular(page: Int) : RequestResult<List<Movie>>
    suspend fun getTopRated(page: Int) : RequestResult<List<Movie>>
    suspend fun getNowPlaying(page: Int) : RequestResult<List<Movie>>
    suspend fun getUpcoming(page: Int) : RequestResult<List<Movie>>
}