package com.example.listagamificada.data.repository

import com.example.listagamificada.data.remote.retrofit.QuoteApi
import com.example.listagamificada.data.remote.retrofit.QuoteResponse
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Locale

// CORREÇÃO: Removida a dependência do Context do construtor.
class QuoteRepository(private val quoteApi: QuoteApi) {

    fun getRandomQuote(): Flow<UiResult<QuoteResponse>> = flow {
        emit(UiResult.Loading)
        try {
            val response = quoteApi.getRandomQuote()
            if (response.isNotEmpty()) {
                val originalQuote = response.first()
                
                // Tenta traduzir, mas não deixa o app quebrar se falhar.
                val finalQuote = translateQuote(originalQuote)
                emit(UiResult.Success(finalQuote))

            } else {
                emit(UiResult.Error(Exception("Nenhuma frase encontrada")))
            }
        } catch (e: Exception) {
            emit(UiResult.Error(e))
        }
    }

    private suspend fun translateQuote(quote: QuoteResponse): QuoteResponse {
        // CORREÇÃO: Envolvendo toda a lógica de tradução em um try-catch para evitar crashes.
        return try {
            val userLanguage = Locale.getDefault().language
            if (userLanguage == "en") return quote

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(userLanguage)
                .build()
            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded().await()

            val translatedText = translator.translate(quote.q).await()
            translator.close()
            
            quote.copy(q = translatedText)
        } catch (t: Throwable) {
            // Se qualquer erro ocorrer (download, tradução, etc), retorna a citação original.
            quote
        }
    }
}
