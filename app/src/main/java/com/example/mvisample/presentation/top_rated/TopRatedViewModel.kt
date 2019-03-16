package com.example.mvisample.presentation.top_rated

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.usecases.GetTopRatedMoviesUseCase
import com.example.mvisample.presentation.movies.*

class TopRatedViewModel(
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase
) : MoviesViewModel() {

    override suspend fun getMovies(page: Int): RequestResult<List<Movie>> {
        return getTopRatedMoviesUseCase.get(page)
    }
}