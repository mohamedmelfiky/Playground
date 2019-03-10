package com.example.mvisample

class MoviesRepo(
    private val api: Api
) {

    suspend fun getNowMovies(): ApiResult {
        return api.getNowPlaying()
    }

}