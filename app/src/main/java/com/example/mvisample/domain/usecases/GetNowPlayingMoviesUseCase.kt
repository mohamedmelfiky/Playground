package com.example.mvisample.domain.usecases

import com.example.domain.entity.Movie
import com.example.mvisample.domain.entity.Result
import com.example.mvisample.domain.repos.IMoviesRepo

class GetNowPlayingMoviesUseCase(
    private val moviesRepo: IMoviesRepo
) {

    suspend fun get(page: Int = 1): Result<List<Movie>> {
        return moviesRepo.getNowPlaying(page)
    }

}