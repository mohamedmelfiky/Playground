package com.example.mvisample.data.repos

import com.example.mvisample.data.remote.Api
import com.example.mvisample.data.remote.safeApiCall
import com.example.mvisample.data.remote.toMovie
import com.example.mvisample.domain.entity.Movie
import com.example.mvisample.domain.entity.Result
import com.example.mvisample.domain.repos.IMoviesRepo

class MoviesRepo(
    private val api: Api
) : IMoviesRepo {

    override suspend fun getNowPlaying(page: Int): Result<List<Movie>> {
        return safeApiCall {
            api.getNowPlaying(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

}