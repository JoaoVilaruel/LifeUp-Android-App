package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.UiResult
import com.example.listagamificada.ui.screens.shop.ShopItem
import com.example.listagamificada.util.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ProfileViewModel(private val repo: ProfileRepository, private val auth: FirebaseAuth) : ViewModel() {

    private val _statsState = MutableStateFlow<UiState<StatsEntity?>>(UiState.Idle)
    val statsState = _statsState.asStateFlow()

    private val _rewardMessage = MutableSharedFlow<String>()
    val rewardMessage = _rewardMessage.asSharedFlow()

    fun getUserId(): String? = auth.currentUser?.uid

    fun loadStats(uid: String) {
        viewModelScope.launch {
            repo.getStats(uid).collect { result ->
                if (result is UiResult.Success) {
                    _statsState.value = UiState.Success(result.data)
                    if (result.data?.userId == uid) { // Ensure we don't trigger for old data
                        checkForDailyReward(uid, result.data)
                    }
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

            if (stats == null || currentTime - lastClaimed > oneDayInMillis) {
                val dailyReward = 25
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

    fun purchaseItem(item: ShopItem) {
        viewModelScope.launch {
            val uid = getUserId()
            if (uid == null) return@launch

            val result = repo.getStats(uid).first()
            if (result is UiResult.Success) {
                val currentStats = result.data
                if (currentStats != null) {
                    if (currentStats.points >= item.price) {
                        if (currentStats.unlockedThemes.split(",").contains(item.id)) {
                            _rewardMessage.emit("Você já possui este item!")
                            return@launch
                        }
                        
                        val newPoints = currentStats.points - item.price
                        val newThemes = currentStats.unlockedThemes + ",${item.id}"
                        val updatedStats = currentStats.copy(points = newPoints, unlockedThemes = newThemes)
                        repo.upsertStats(updatedStats)
                        _rewardMessage.emit("Item '${item.name}' comprado com sucesso!")
                    } else {
                        _rewardMessage.emit("Pontos insuficientes!")
                    }
                }
            }
        }
    }
}
