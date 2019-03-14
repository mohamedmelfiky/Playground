package com.example.mvisample.presentation.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.domain.entity.Movie
import com.example.mvisample.R
import kotlinx.android.synthetic.main.item_movie.view.*

class NewMoviesAdapter : ListAdapter<Movie, MovieViewHolder>(
    DiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}

object DiffCallback : DiffUtil.ItemCallback<Movie>() {

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}

class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(movie: Movie) {
        Glide.with(itemView.moviePosterImgV)
            .load(movie.poster)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.moviePosterImgV)

        itemView.movieTitleTv.text = movie.title
        itemView.setOnClickListener {
            val action =
                MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(
                    movie.id
                )
            itemView.findNavController().navigate(action)
        }
    }

}