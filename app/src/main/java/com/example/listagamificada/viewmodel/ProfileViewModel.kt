package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.data.repository.ProfileRepository

import com.example.listagamificada.util.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class ProfileUiState(
    val stats: StatsEntity? = null,
    val tasksCompletedCount: Int = 0,
    val tasksPendingCount: Int = 0,
    val weeklyActivity: Map<String, Int> = emptyMap(),
    val favoriteCategory: String = "N/A",
    val xpProgress: Float = 0f,
    val xpForNextLevel: Int = 100,
    val currentXp: Int = 0
)

class ProfileViewModel(private val repository: ProfileRepository, private val auth: FirebaseAuth) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProfileUiState>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _rewardMessage = MutableSharedFlow<String>()
    val rewardMessage = _rewardMessage.asSharedFlow()

    private fun getUserId(): String? = auth.currentUser?.uid

    fun loadProfileData() {
        viewModelScope.launch {
            val userId = getUserId() ?: return@launch
            _uiState.value = UiState.Loading

            combine(
                repository.getStats(userId),
                repository.getTasks(userId)
            ) { stats, tasks ->
                val completed = tasks.count { it.completed }
                val pending = tasks.size - completed

                val weekly = mutableMapOf<String, Int>()
                val cal = Calendar.getInstance()
                tasks.filter { it.completed }.forEach { task ->
                    task.dueDate?.let { dueDate ->
                        cal.timeInMillis = dueDate
                        val dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
                        if (dayOfWeek.isNotBlank()) {
                            weekly[dayOfWeek] = weekly.getOrDefault(dayOfWeek, 0) + 1
                        }
                    }
                }

                val favCategory = tasks.groupBy { it.category }.maxByOrNull { it.value.size }?.key ?: "N/A"

                val xpForNextLevel = stats?.level?.let { it * 100 } ?: 100
                val currentXp = stats?.xp ?: 0
                val xpProgress = if (xpForNextLevel > 0) (currentXp.toFloat() / xpForNextLevel) else 0f

                ProfileUiState(
                    stats = stats,
                    tasksCompletedCount = completed,
                    tasksPendingCount = pending,
                    weeklyActivity = weekly,
                    favoriteCategory = favCategory,
                    xpProgress = xpProgress,
                    xpForNextLevel = xpForNextLevel,
                    currentXp = currentXp
                )
            }.catch { e ->
                _uiState.value = UiState.Error(e.message ?: "Erro ao carregar perfil")
            }.collect { profileState ->
                _uiState.value = UiState.Success(profileState)
            }
        }
    }


}