package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.remote.retrofit.QuoteResponse
import com.example.listagamificada.data.repository.QuoteRepository
import com.example.listagamificada.data.repository.UiResult
import com.example.listagamificada.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QuoteViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _quoteState = MutableStateFlow<UiState<QuoteResponse>>(UiState.Idle)
    val quoteState: StateFlow<UiState<QuoteResponse>> = _quoteState

    fun fetchRandomQuote() {
        viewModelScope.launch {
            repository.getRandomQuote().collect { result ->
                val uiState = when (result) {
                    is UiResult.Success -> UiState.Success(result.data!!)
                    is UiResult.Error -> UiState.Error(result.exception?.message ?: "Erro desconhecido")
                    is UiResult.Loading -> UiState.Loading
                }
                _quoteState.value = uiState
            }
        }
    }
}
