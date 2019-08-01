package com.example.mvisample.presentation.top_rated

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
@FlowPreview
class TopRatedFragment : MoviesFragment() {

    override val viewModel: TopRatedViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = TopRatedFragmentDirections.actionTopRatedFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }
}