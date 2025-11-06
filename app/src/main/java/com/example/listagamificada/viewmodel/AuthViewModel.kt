package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<FirebaseUser?>>(UiState.Idle)
    val loginState: StateFlow<UiState<FirebaseUser?>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _loginState.value = UiState.Success(result.user)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Falha ao fazer login", e)
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _loginState.value = UiState.Success(result.user)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Falha ao registrar", e)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _loginState.value = UiState.Idle
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun getUserId(): String? = auth.currentUser?.uid
}
