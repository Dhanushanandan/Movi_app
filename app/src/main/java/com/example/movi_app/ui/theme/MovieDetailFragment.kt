package com.example.movi_app.ui.theme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.movi_app.R
import com.example.movi_app.RetrofitClient
import com.example.movi_app.databinding.FragmentMovieDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val args: MovieDetailFragmentArgs by navArgs()
    private val apiKey = "f0be361259ca3d1e96daeed30f539267"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        loadMovieDetails(args.movieId, view)

        return view
    }

    private fun loadMovieDetails(movieId: Int, view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getMovieDetails(movieId, apiKey)
                withContext(Dispatchers.Main) {
                    binding.textTitle.text = response.title
                    binding.textReleaseDate.text = "Release Date: ${response.releaseDate ?: "N/A"}"
                    binding.textRating.text = "Rating: ${response.rating}"
                    binding.textOverview.text = "Overview: ${response.overview}"
                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500${response.posterPath}")
                        .into(binding.imagePoster)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.textTitle.text = "Error loading movie details."
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}