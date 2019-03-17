package com.example.data.repos

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.repos.IMoviesRepo
import com.example.data.remote.RemoteDataSource

class MoviesRepo(
    private val remoteDataSource: RemoteDataSource
) : IMoviesRepo {

    override suspend fun getPopular(page: Int): RequestResult<List<Movie>> {
        return remoteDataSource.getPopular(page)
    }

    override suspend fun getTopRated(page: Int): RequestResult<List<Movie>> {
        return remoteDataSource.getTopRated(page)
    }

    override suspend fun getNowPlaying(page: Int): RequestResult<List<Movie>> {
        return remoteDataSource.getNowPlaying(page)
    }

    override suspend fun getUpcoming(page: Int): RequestResult<List<Movie>> {
        return remoteDataSource.getUpcoming(page)
    }

}