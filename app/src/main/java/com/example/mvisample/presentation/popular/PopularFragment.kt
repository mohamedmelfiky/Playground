package com.example.mvisample.presentation.popular

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PopularFragment : MoviesFragment<PopularViewModel>() {

    override val viewModel: PopularViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = PopularFragmentDirections.actionPopularFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }

}