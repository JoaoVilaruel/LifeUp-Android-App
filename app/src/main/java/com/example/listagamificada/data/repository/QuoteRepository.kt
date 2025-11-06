package com.example.listagamificada.data.repository

import android.content.Context
import com.example.listagamificada.data.remote.retrofit.QuoteApi
import com.example.listagamificada.data.remote.retrofit.QuoteResponse
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Locale

class QuoteRepository(private val quoteApi: QuoteApi, private val context: Context) {

    fun getRandomQuote(): Flow<UiResult<QuoteResponse>> = flow {
        emit(UiResult.Loading)
        try {
            val response = quoteApi.getRandomQuote()
            if (response.isNotEmpty()) {
                val originalQuote = response.first()
                emit(UiResult.Success(originalQuote)) // Emit original quote first

                // Translate in the background and emit again if successful
                val translatedQuote = translateQuote(originalQuote)
                if (translatedQuote.q != originalQuote.q) { // Check if translation happened
                    emit(UiResult.Success(translatedQuote))
                }
            } else {
                emit(UiResult.Error(Exception("Nenhuma frase encontrada")))
            }
        } catch (e: Exception) {
            emit(UiResult.Error(e))
        }
    }

    private suspend fun translateQuote(quote: QuoteResponse): QuoteResponse {
        val userLanguage = Locale.getDefault().language
        if (userLanguage == "en") return quote

        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(userLanguage)
                .build()
            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded().await() // Let it download without conditions for now

            val translatedText = translator.translate(quote.q).await()
            translator.close()
            
            quote.copy(q = translatedText)
        } catch (e: Exception) {
            quote // Return original on failure
        }
    }
}
