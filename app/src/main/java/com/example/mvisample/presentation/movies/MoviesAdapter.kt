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
import com.example.domain.entity.MovieItem
import com.example.domain.entity.MovieLoading
import com.example.mvisample.R
import com.example.mvisample.util.clicks
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.coroutines.flow.map

const val LOADING_VIEW_TYPE = 0
const val MOVIE_VIEW_TYPE = 1

class MoviesAdapter(
    private val clickListener: OnMovieClickListener
) : ListAdapter<MovieItem, RecyclerView.ViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            LOADING_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
                MoviesViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MoviesViewHolder -> holder.bindTo(clickListener, getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when(item) {
            MovieLoading -> LOADING_VIEW_TYPE
            is Movie -> MOVIE_VIEW_TYPE
        }
    }

}

object DiffCallback : DiffUtil.ItemCallback<MovieItem>() {

    override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
        if (oldItem is Movie && newItem is Movie) {
            return oldItem.id == newItem.id
        }

        return false
    }

    override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
        if (oldItem is Movie && newItem is Movie) {
            return oldItem == newItem
        }

        return false
    }
}

class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(clickListener: OnMovieClickListener, movie: MovieItem) {
        if (movie is Movie) {
            Glide.with(itemView.moviePosterImgV)
                .load(movie.poster)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.moviePosterImgV)

            itemView.movieTitleTv.text = movie.title
            itemView.setOnClickListener { clickListener.onMovieClicked(movie) }
        }
    }

}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

interface OnMovieClickListener {
    fun onMovieClicked(movie: Movie)
}