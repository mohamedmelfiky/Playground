package com.example.domain.usecases

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.repos.IMoviesRepo

class GetTopRatedMoviesUseCase(
    private val moviesRepo: IMoviesRepo
) {

    suspend fun get(page: Int = 1): RequestResult<List<Movie>> {
        return moviesRepo.getTopRated(page)
    }

}