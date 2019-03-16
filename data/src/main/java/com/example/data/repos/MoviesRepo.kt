package com.example.data.repos

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.repos.IMoviesRepo
import com.example.data.remote.Api
import com.example.data.remote.safeApiCall
import com.example.data.remote.toMovie

class MoviesRepo(
    private val api: Api
) : IMoviesRepo {

    override suspend fun getPopular(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getPopular(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

    override suspend fun getTopRated(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getTopRated(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

    override suspend fun getNowPlaying(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getNowPlaying(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

    override suspend fun getUpcoming(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getUpcoming(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

}