package com.example.mvisample.presentation.popular

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.usecases.GetPopularMoviesUseCase
import com.example.mvisample.presentation.movies.*

class PopularViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : MoviesViewModel() {

    override suspend fun getMovies(page: Int): RequestResult<List<Movie>> {
        return getPopularMoviesUseCase.get(page)
    }

}