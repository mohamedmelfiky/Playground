package com.example.mvisample.presentation.upcoming

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
@FlowPreview
class UpcomingFragment : MoviesFragment() {

    override val viewModel: UpcomingViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = UpcomingFragmentDirections.actionUpcomingFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }

}