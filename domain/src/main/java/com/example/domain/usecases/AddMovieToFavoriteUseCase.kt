package com.example.domain.usecases

import com.example.domain.entity.Movie
import com.example.domain.entity.RequestResult
import com.example.domain.repos.IMoviesRepo
import com.example.domain.udf.BaseAction
import com.example.domain.udf.BaseResult
import com.example.domain.udf.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

data class AddMovieToFavoriteAction(val movie: Movie) : BaseAction

sealed class AddMovieToFavoriteResult : BaseResult {
    object Loading : AddMovieToFavoriteResult()
    object Success : AddMovieToFavoriteResult()
    data class Error(val exception: Exception) : AddMovieToFavoriteResult()
}

@ExperimentalCoroutinesApi
class AddMovieToFavoriteUseCase(
    private val moviesRepo: IMoviesRepo
) : BaseUseCase<AddMovieToFavoriteAction, AddMovieToFavoriteResult>() {

    override fun invoke(action: AddMovieToFavoriteAction): Flow<AddMovieToFavoriteResult> =
        flow { emit(moviesRepo.addToFavorite(action.movie)) }
            .map { response ->
                when (response) {
                    is RequestResult.Success -> AddMovieToFavoriteResult.Success
                    is RequestResult.Error -> AddMovieToFavoriteResult.Error(response.exception)
                }
            }.flowOn(Dispatchers.IO)
            .onStart { AddMovieToFavoriteResult.Loading }

}