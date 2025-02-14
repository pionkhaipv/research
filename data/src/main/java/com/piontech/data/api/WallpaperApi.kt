package com.piontech.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WallpaperApi {
    @GET("/movies")
    suspend fun getMovies(@Query("id") movieIds: List<Int>): List<String>
}