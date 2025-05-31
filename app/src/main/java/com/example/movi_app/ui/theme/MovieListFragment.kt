package com.example.movi_app.ui.theme


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movi_app.R
import com.example.movi_app.RetrofitClient
import com.example.movi_app.adapter.MovieAdapter
import com.example.movi_app.model.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private val movieList = mutableListOf<Movie>()
    private val apiKey = "f0be361259ca3d1e96daeed30f539267" // Replace with your TMDB API key

    // Map genre names to TMDB genre IDs
    private val genreMap = mapOf(
        "Action" to 28,
        "Comedy" to 35,
        "Drama" to 18
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_movies)
        recyclerView.layoutManager = LinearLayoutManager(context)
        movieAdapter = MovieAdapter(movieList) { movie ->
            val action = MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movie.id)
            findNavController().navigate(action)
        }
        recyclerView.adapter = movieAdapter

        val spinnerCategory = view.findViewById<Spinner>(R.id.spinner_category)
        val spinnerGenre = view.findViewById<Spinner>(R.id.spinner_genre)
        val btnApplyFilter = view.findViewById<Button>(R.id.btn_apply_filter)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.movie_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.movie_genres,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGenre.adapter = adapter
        }

        btnApplyFilter.setOnClickListener {
            loadMovies(spinnerCategory.selectedItem.toString(), spinnerGenre.selectedItem.toString())
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

                // Filter movies by genre
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}