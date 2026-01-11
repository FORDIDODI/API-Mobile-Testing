package com.example.api_mobile_testing.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val POSTER_SIZE = "w500"
    const val BACKDROP_SIZE = "w780"
    const val PROFILE_SIZE = "w185"

    // GANTI DENGAN API KEY V3 ANDA (bukan Bearer Token)
    // Format: 807b0e64ad0f5ec355402db22423a127
    const val API_KEY = "807b0e64ad0f5ec355402db22423a127"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: TmdbApiService = retrofit.create(TmdbApiService::class.java)

    fun getPosterUrl(path: String?): String {
        return if (path != null) {
            "$IMAGE_BASE_URL$POSTER_SIZE$path"
        } else {
            ""
        }
    }

    fun getBackdropUrl(path: String?): String {
        return if (path != null) {
            "$IMAGE_BASE_URL$BACKDROP_SIZE$path"
        } else {
            ""
        }
    }

    fun getProfileUrl(path: String?): String {
        return if (path != null) {
            "$IMAGE_BASE_URL$PROFILE_SIZE$path"
        } else {
            ""
        }
    }
}