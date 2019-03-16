package com.example.mvisample.presentation.now_playing

import com.example.domain.entity.Movie
import com.example.domain.usecases.GetNowPlayingMoviesUseCase
import com.example.domain.entity.RequestResult
import com.example.mvisample.presentation.movies.*

class NowPlayingViewModel(
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase
) : MoviesViewModel() {

    override suspend fun getMovies(page: Int): RequestResult<List<Movie>> {
        return getNowPlayingMoviesUseCase.get(page)
    }

}