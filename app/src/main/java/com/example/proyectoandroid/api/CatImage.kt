package com.example.proyectoandroid.api

import retrofit2.http.GET

data class CatImage(val url: String)

interface CatApiService {
    @GET("v1/images/search?limit=20")
    suspend fun getCatImages(): List<CatImage>
}