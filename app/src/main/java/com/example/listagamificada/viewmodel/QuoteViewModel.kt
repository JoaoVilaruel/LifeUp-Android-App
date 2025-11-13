package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.remote.retrofit.QuoteResponse
import com.example.listagamificada.data.repository.QuoteRepository
import com.example.listagamificada.data.repository.UiResult
import com.example.listagamificada.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _quoteState = MutableStateFlow<UiState<QuoteResponse>>(UiState.Idle)
    val quoteState: StateFlow<UiState<QuoteResponse>> = _quoteState

    init {
        fetchRandomQuote()
    }

    fun fetchRandomQuote() {
        viewModelScope.launch {
            repository.getRandomQuote().collect { result ->
                val uiState = when (result) {
                    is UiResult.Success -> {
                        // CORREÇÃO: result.data já é o objeto QuoteResponse, não uma lista.
                        val quote = result.data
                        if (quote != null) {
                            UiState.Success(quote)
                        } else {
                            UiState.Error("Resposta da API está vazia ou em formato inesperado.")
                        }
                    }
                    is UiResult.Error -> UiState.Error(result.exception?.message ?: "Erro desconhecido")
                    is UiResult.Loading -> UiState.Loading
                }
                _quoteState.value = uiState
            }
        }
    }
}