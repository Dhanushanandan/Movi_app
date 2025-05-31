package com.example.movi_app.ui.theme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.movi_app.R
import com.example.movi_app.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailFragment : Fragment() {

    private val args: MovieDetailFragmentArgs by navArgs()
    private val apiKey = "f0be361259ca3d1e96daeed30f539267" // Replace with your TMDB API key

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_detail, container, false)

        val movieId = args.movieId
        loadMovieDetails(movieId, view)

        return view
    }

    private fun loadMovieDetails(movieId: Int, view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getMovieDetails(movieId, apiKey)
                withContext(Dispatchers.Main) {
                    view.findViewById<TextView>(R.id.text_title).text = response.title
                    view.findViewById<TextView>(R.id.text_release_date).text = "Release Date: ${response.releaseDate}"
                    view.findViewById<TextView>(R.id.text_rating).text = "Rating: ${response.rating}"
                    view.findViewById<TextView>(R.id.text_overview).text = "Overview: ${response.overview}"
                    Picasso.get().load("https://image.tmdb.org/t/p/w500${response.posterPath}").into(view.findViewById<ImageView>(R.id.image_poster))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}