package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.repository.AppRepository
import com.example.listagamificada.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val repository: AppRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<FirebaseUser?>>(UiState.Idle)
    val loginState: StateFlow<UiState<FirebaseUser?>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    var stats = repository.getStatsFromFirestore(user.uid)
                    if (stats == null) {
                        val defaultName = user.displayName ?: "Jogador"
                        stats = StatsEntity(userId = user.uid, userName = defaultName, level = 1, xp = 0, points = 0)
                        repository.upsertStats(stats)
                    } else if (stats.userName.isBlank()) {
                        val defaultName = user.displayName ?: "Jogador"
                        repository.upsertStats(stats.copy(userName = defaultName))
                    }
                }
                _loginState.value = UiState.Success(user)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Falha ao fazer login", e)
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user.updateProfile(profileUpdates).await()

                    val newStats = StatsEntity(
                        userId = user.uid,
                        userName = name,
                        level = 1,
                        xp = 0,
                        points = 0
                    )
                    repository.upsertStats(newStats)
                }
                _loginState.value = UiState.Success(user)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Falha ao registrar", e)
            }
        }
    }

    // CORREÇÃO: Adicionando a função que faltava
    suspend fun updateDisplayName(newName: String): Boolean {
        val user = auth.currentUser
        if (user == null || newName.isBlank()) {
            return false
        }

        return try {
            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }
            user.updateProfile(profileUpdates).await()
            true // Sucesso
        } catch (e: Exception) {
            false // Falha
        }
    }

    fun logout() {
        auth.signOut()
        _loginState.value = UiState.Idle
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun getUserId(): String? = auth.currentUser?.uid
}
