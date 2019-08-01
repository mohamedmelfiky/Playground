package com.example.mvisample.presentation.now_playing

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
@FlowPreview
class NowPlayingFragment : MoviesFragment() {

    override val viewModel: NowPlayingViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = NowPlayingFragmentDirections.actionNowPlaingFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }

}