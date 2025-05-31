package com.example.movi_app

import com.example.movi_app.api.TMDBApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: TMDBApi by lazy {
        retrofit.create(TMDBApi::class.java)
    }
}