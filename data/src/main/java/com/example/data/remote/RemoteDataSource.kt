package com.example.data.remote

import com.example.data.entity.toMovie
import com.example.data.remote.api.Api
import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult

class RemoteDataSource(
    private val api: Api
) {

    suspend fun getPopular(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getPopular(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

    suspend fun getTopRated(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getTopRated(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

    suspend fun getNowPlaying(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getNowPlaying(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

    suspend fun getUpcoming(page: Int): RequestResult<List<Movie>> {
        return safeApiCall {
            api.getUpcoming(page = page).movies?.mapNotNull { it?.toMovie() } ?: emptyList()
        }
    }

}