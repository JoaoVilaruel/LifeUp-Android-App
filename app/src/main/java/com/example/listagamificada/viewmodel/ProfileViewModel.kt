package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.UiResult
import com.example.listagamificada.util.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ProfileViewModel(private val repo: ProfileRepository, private val authViewModel: AuthViewModel) : ViewModel() {

    private val _statsState = MutableStateFlow<UiState<StatsEntity?>>(UiState.Idle)
    val statsState = _statsState.asStateFlow()

    private val _rewardMessage = MutableSharedFlow<String>()
    val rewardMessage = _rewardMessage.asSharedFlow()

    fun getUserId(): String? = authViewModel.getUserId()

    fun loadStats(uid: String) {
        viewModelScope.launch {
            repo.getStats(uid).collect { result ->
                if (result is UiResult.Success) {
                    _statsState.value = UiState.Success(result.data)
                    checkForDailyReward(uid, result.data)
                } else if (result is UiResult.Error) {
                    _statsState.value = UiState.Error(result.exception?.message ?: "Erro")
                }
            }
        }
    }

    private fun checkForDailyReward(uid: String, stats: StatsEntity?) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val lastClaimed = stats?.lastClaimedDaily ?: 0L
            val oneDayInMillis = TimeUnit.HOURS.toMillis(24)
            val dailyReward = 25

            if (currentTime - lastClaimed > oneDayInMillis) {
                val currentPoints = stats?.points ?: 0
                val newStats = stats?.copy(
                    points = currentPoints + dailyReward,
                    lastClaimedDaily = currentTime
                ) ?: StatsEntity(userId = uid, points = dailyReward, lastClaimedDaily = currentTime)

                repo.upsertStats(newStats)
                _rewardMessage.emit("🎉 Recompensa Diária +$dailyReward Pontos!")
            }
        }
    }

    fun addPoints(uid: String, points: Int) {
        viewModelScope.launch {
            val result = repo.getStats(uid).first()
            if (result is UiResult.Success) {
                val currentStats = result.data
                if (currentStats != null) {
                    val updated = currentStats.copy(points = currentStats.points + points)
                    repo.upsertStats(updated)
                }
            }
        }
    }
}
