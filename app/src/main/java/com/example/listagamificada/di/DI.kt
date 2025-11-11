// Define o pacote para a injeção de dependência.
package com.example.listagamificada.di

// Importa as classes necessárias do Retrofit e da API de cotações.
import com.example.listagamificada.data.remote.retrofit.QuoteApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton para fornecer as dependências da aplicação (Injeção de Dependência).
object DI {
    // Cria uma instância do Retrofit para fazer requisições de rede.
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/") // Define a URL base para as requisições da API.
        .addConverterFactory(GsonConverterFactory.create()) // Adiciona um conversor para transformar JSON em objetos Kotlin.
        .build() // Constrói a instância do Retrofit.

    // Cria e expõe a implementação da interface da API de cotações.
    val quoteApi: QuoteApi = retrofit.create(QuoteApi::class.java)
}
