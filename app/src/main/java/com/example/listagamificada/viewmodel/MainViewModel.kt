package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.data.repository.AppRepository
import com.example.listagamificada.util.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<UiState<List<TaskEntity>>>(UiState.Idle)
    val tasks = _tasks.asStateFlow()

    private val _stats = MutableStateFlow<UiState<StatsEntity?>>(UiState.Idle)
    val stats = _stats.asStateFlow()

    private val _ranking = MutableStateFlow<UiState<List<StatsEntity>>>(UiState.Idle)
    val ranking = _ranking.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun toggleTaskCompletion(task: TaskEntity, userId: String) {
        viewModelScope.launch {
            val toggledTask = task.copy(completed = !task.completed)
            repository.updateTask(toggledTask)

            // Only award XP when completing, not when un-completing
            if (toggledTask.completed) {
                val xpGained = when (task.difficulty) {
                    "Fácil" -> 10
                    "Médio" -> 25
                    "Difícil" -> 50
                    else -> 0
                }

                if (xpGained > 0) {
                    val currentStats = repository.getStats(userId).first()
                    if (currentStats != null) {
                        val newXp = currentStats.xp + xpGained
                        val xpToNextLevel = 100
                        
                        val newLevel = currentStats.level + (newXp / xpToNextLevel)
                        val remainingXp = newXp % xpToNextLevel

                        val updatedStats = currentStats.copy(
                            level = newLevel,
                            xp = remainingXp,
                            points = currentStats.points + xpGained // Also add to total points
                        )
                        repository.upsertStats(updatedStats)
                        _uiEvent.emit("🎉 +$xpGained XP!")
                    }
                }
            }
        }
    }
    
    fun loadRanking() {
        viewModelScope.launch {
            _ranking.value = UiState.Loading
            repository.getRanking()
                .catch { e -> _ranking.value = UiState.Error(e.message ?: "Failed to load ranking") }
                .collect { rankingList -> _ranking.value = UiState.Success(rankingList) }
        }
    }

    fun checkForDailyReward(userId: String) {
        viewModelScope.launch {
            val currentStats = repository.getStats(userId).first()
            val currentTime = System.currentTimeMillis()
            val lastClaimed = currentStats?.lastClaimedDaily ?: 0L
            val oneDayInMillis = TimeUnit.HOURS.toMillis(24)

            if (currentStats == null || currentTime - lastClaimed > oneDayInMillis) {
                val dailyReward = 25
                val newPoints = (currentStats?.points ?: 0) + dailyReward
                val newStats = currentStats?.copy(
                    points = newPoints,
                    lastClaimedDaily = currentTime
                ) ?: StatsEntity(userId = userId, points = dailyReward, lastClaimedDaily = currentTime)
                repository.upsertStats(newStats)
                _uiEvent.emit("🎉 Recompensa Diária +$dailyReward Pontos!")
            }
        }
    }

    fun loadTasksForUser(userId: String) {
        viewModelScope.launch {
            repository.getTasks(userId).collect { _tasks.value = UiState.Success(it) }
        }
    }

    fun loadStatsForUser(userId: String) {
        viewModelScope.launch {
            repository.getStats(userId).collect { _stats.value = UiState.Success(it) }
        }
    }
}
