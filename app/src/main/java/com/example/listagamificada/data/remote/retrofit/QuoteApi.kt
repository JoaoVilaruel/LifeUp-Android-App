package com.example.listagamificada.data.remote.retrofit

import retrofit2.http.GET

interface QuoteApi {
    @GET("https://zenquotes.io/api/random")
    suspend fun getRandomQuote(): List<QuoteResponse>
}

data class QuoteResponse(
    val q: String, // texto
    val a: String  // autor
)
