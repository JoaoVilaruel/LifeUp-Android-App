package com.example.listagamificada.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.TaskRepository
import com.example.listagamificada.util.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val coinReward: Int,
    var currentProgress: Int,
    val targetProgress: Int,
    val icon: ImageVector,
    var isClaimed: Boolean = false,
    val type: String
)

data class ChallengesUiState(
    val challenges: List<Challenge> = emptyList(),
    val userCoins: Int = 0,
    val completedCount: Int = 0
)

class ChallengesViewModel(
    private val profileRepository: ProfileRepository,
    private val taskRepository: TaskRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ChallengesUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ChallengesUiState>> = _uiState.asStateFlow()

    private var currentChallenges: List<Challenge> = emptyList()

    init {
        observeChallenges()
    }

    private fun observeChallenges() {
        viewModelScope.launch {
            val uid = authViewModel.getUserId() ?: run {
                _uiState.value = UiState.Error("Usuário não autenticado.")
                return@launch
            }

            combine(
                profileRepository.getStats(uid),
                taskRepository.getRawTasks(uid),
                profileRepository.getClaimedChallengeIds(uid)
            ) { stats, tasks, claimedIds ->
                if (stats == null) {
                    throw IllegalStateException("Estatísticas do usuário não encontradas.")
                }

                val completedTasksToday = tasks.filter { it.completed }
                val hardTasksCompleted = completedTasksToday.filter { it.difficulty == "Difícil" }

                val updatedChallenges = listOf(
                    Challenge("first_step", "Primeiro Passo", "Complete 1 tarefa hoje", 10, 0, 1, Icons.Default.PlayArrow, type = "total_tasks"),
                    Challenge("productive", "Produtivo", "Complete 3 tarefas hoje", 30, 0, 3, Icons.Default.PlayArrow, type = "total_tasks"),
                    Challenge("unstoppable", "Imparável", "Complete 5 tarefas hoje", 50, 0, 5, Icons.Default.PlayArrow, type = "total_tasks"),
                    Challenge("challenger", "Desafiador", "Complete uma tarefa difícil", 25, 0, 1, Icons.Default.PlayArrow, type = "hard_tasks"),
                    Challenge("consistency", "Consistência", "Mantenha sua sequência ativa", 15, 0, 3, Icons.Default.PlayArrow, type = "login_streak")
                ).onEach { challenge ->
                    challenge.isClaimed = claimedIds.contains(challenge.id)
                    when (challenge.type) {
                        "total_tasks" -> challenge.currentProgress = completedTasksToday.size
                        "hard_tasks" -> challenge.currentProgress = hardTasksCompleted.size
                    }
                }
                
                currentChallenges = updatedChallenges
                val completedCount = updatedChallenges.count { it.currentProgress >= it.targetProgress }

                ChallengesUiState(
                    challenges = updatedChallenges,
                    userCoins = stats.coins,
                    completedCount = completedCount
                )
            }
            .onStart { _uiState.value = UiState.Loading }
            .catch { e -> 
                val errorMessage = e.message ?: "Erro desconhecido"
                _uiState.value = UiState.Error("Falha ao carregar desafios. Verifique as Regras de Segurança do Firestore. Detalhes: $errorMessage") 
            }
            .collect { data ->
                _uiState.value = UiState.Success(data)
            }
        }
    }

    fun claimReward(challengeId: String) {
        viewModelScope.launch {
            val uid = authViewModel.getUserId() ?: return@launch
            val challenge = currentChallenges.find { it.id == challengeId }
            
            if (challenge == null || challenge.isClaimed || challenge.currentProgress < challenge.targetProgress) {
                return@launch
            }

            val stats = profileRepository.getStats(uid).first()
            if (stats != null) {
                val updatedStats = stats.copy(coins = stats.coins + challenge.coinReward)
                profileRepository.upsertStats(updatedStats)
                profileRepository.claimChallenge(uid, challengeId)
            }
        }
    }
}