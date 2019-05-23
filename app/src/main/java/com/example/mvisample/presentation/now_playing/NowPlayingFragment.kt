package com.example.mvisample.presentation.now_playing

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NowPlayingFragment : MoviesFragment() {

    override val viewModel: NowPlayingViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = NowPlayingFragmentDirections.actionNowPlaingFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }

}