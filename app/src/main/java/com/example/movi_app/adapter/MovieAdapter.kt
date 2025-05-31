package com.example.movi_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movi_app.R
import com.example.movi_app.model.Movie
import com.squareup.picasso.Picasso

class MovieAdapter(
    private val movieList: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePoster: ImageView = itemView.findViewById(R.id.image_poster_item)
        val textTitle: TextView = itemView.findViewById(R.id.text_title_item)
        val textReleaseDate: TextView = itemView.findViewById(R.id.text_release_date_item)
        val textRating: TextView = itemView.findViewById(R.id.text_rating_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.textTitle.text = movie.title
        holder.textReleaseDate.text = "Release Date: ${movie.releaseDate}"
        holder.textRating.text = "Rating: ${movie.rating}"
        Picasso.get().load("https://image.tmdb.org/t/p/w500${movie.posterPath}").into(holder.imagePoster)
        holder.itemView.setOnClickListener { onItemClick(movie) }
    }

    override fun getItemCount(): Int = movieList.size
}