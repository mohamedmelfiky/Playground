package com.example.mvisample.presentation.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.domain.entity.Movie
import com.example.mvisample.R
import kotlinx.android.synthetic.main.item_movie.view.*

class MoviesAdapter(
    private val clickListener: OnMovieClickListener
) : ListAdapter<Movie, MoviesViewHolder>(
    DiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_test, parent, false)
        return MoviesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bindTo(clickListener, getItem(position))
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

class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(clickListener: OnMovieClickListener, movie: Movie) {
        Glide.with(itemView.moviePosterImgV)
            .load(movie.poster)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.moviePosterImgV)

        itemView.movieTitleTv.text = movie.title
        itemView.setOnClickListener { clickListener.onMovieClicked(movie) }
    }

}

interface OnMovieClickListener {
    fun onMovieClicked(movie: Movie)
}