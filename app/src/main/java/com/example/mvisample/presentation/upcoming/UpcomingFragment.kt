package com.example.mvisample.presentation.upcoming

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UpcomingFragment : MoviesFragment<UpcomingViewModel>() {

    override val viewModel: UpcomingViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = UpcomingFragmentDirections.actionUpcomingFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }

}