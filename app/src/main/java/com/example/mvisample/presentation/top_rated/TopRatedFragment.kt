package com.example.mvisample.presentation.top_rated

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TopRatedFragment : MoviesFragment<TopRatedViewModel>() {

    override val viewModel: TopRatedViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = TopRatedFragmentDirections.actionTopRatedFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }
}