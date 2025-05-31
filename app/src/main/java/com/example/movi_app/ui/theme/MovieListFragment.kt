package com.example.movi_app.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movi_app.R
import com.example.movi_app.RetrofitClient
import com.example.movi_app.adapter.MovieAdapter
import com.example.movi_app.databinding.FragmentMovieListBinding
import com.example.movi_app.model.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieAdapter: MovieAdapter
    private val movieList = mutableListOf<Movie>()
    private val apiKey = "f0be361259ca3d1e96daeed30f539267"

    private val genreMap = mapOf(
        "Action" to 28,
        "Comedy" to 35,
        "Drama" to 18
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(context)
        movieAdapter = MovieAdapter(movieList) { movie ->
            try {
                val action = MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movie.id)
                findNavController().navigate(action)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.recyclerViewMovies.adapter = movieAdapter

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.movie_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.movie_genres,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGenre.adapter = adapter
        }

        binding.btnApplyFilter.setOnClickListener {
            loadMovies(binding.spinnerCategory.selectedItem.toString(), binding.spinnerGenre.selectedItem.toString())
        }

        loadMovies("Popular", "All")

        return view
    }

    private fun loadMovies(category: String, genre: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = when (category) {
                    "Popular" -> RetrofitClient.api.getPopularMovies(apiKey, 1)
                    "Upcoming" -> RetrofitClient.api.getUpcomingMovies(apiKey, 1)
                    else -> RetrofitClient.api.getPopularMovies(apiKey, 1)
                }

                val filteredList = if (genre == "All") {
                    response.movies
                } else {
                    val genreId = genreMap[genre] ?: 0
                    response.movies.filter { movie ->
                        movie.genreIds.contains(genreId)
                    }
                }

                withContext(Dispatchers.Main) {
                    movieList.clear()
                    movieList.addAll(filteredList)
                    movieAdapter.notifyDataSetChanged()
                    binding.recyclerViewMovies.visibility = View.VISIBLE
                    binding.errorText.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.recyclerViewMovies.visibility = View.GONE
                    binding.errorText.visibility = View.VISIBLE
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}