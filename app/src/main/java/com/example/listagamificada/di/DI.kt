package com.example.listagamificada.di

import com.example.listagamificada.data.remote.retrofit.QuoteApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DI {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val quoteApi: QuoteApi = retrofit.create(QuoteApi::class.java)
}
