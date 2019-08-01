package com.example.mvisample.presentation.popular

import androidx.navigation.fragment.findNavController
import com.example.domain.entity.Movie
import com.example.mvisample.presentation.movies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
@FlowPreview
class PopularFragment : MoviesFragment() {

    override val viewModel: PopularViewModel by sharedViewModel()

    override fun onMovieClicked(movie: Movie) {
        val action = PopularFragmentDirections.actionPopularFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }

}