package com.example.mvisample.presentation.upcoming

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.usecases.GetUpcomingMoviesUseCase
import com.example.mvisample.presentation.movies.*

class UpcomingViewModel(
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase
) : MoviesViewModel() {

    override suspend fun getMovies(page: Int): RequestResult<List<Movie>> {
        return getUpcomingMoviesUseCase.get(page)
    }

}