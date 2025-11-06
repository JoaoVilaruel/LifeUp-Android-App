package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.TaskRepository
import com.example.listagamificada.data.repository.UiResult
import com.example.listagamificada.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepo: TaskRepository, 
    private val profileRepo: ProfileRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _tasks = MutableStateFlow<UiState<List<TaskEntity>>>(UiState.Idle)
    val tasks: StateFlow<UiState<List<TaskEntity>>> = _tasks

    fun getUserId(): String? = authViewModel.getUserId()

    fun loadTasks(uid: String) {
        viewModelScope.launch {
            taskRepo.getTasks(uid).collect { result ->
                _tasks.value = result.toUiState()
            }
        }
    }

    fun addTask(task: TaskEntity) = viewModelScope.launch {
        taskRepo.addTask(task)
    }

    fun updateTask(task: TaskEntity) = viewModelScope.launch {
        taskRepo.updateTask(task)

        // --- Reward Logic ---
        if (task.completed) {
            val points = when (task.difficulty) {
                "Fácil" -> 10
                "Médio" -> 25
                "Difícil" -> 50
                else -> 0
            }
            
            val uid = getUserId()
            if (uid != null && points > 0) {
                val result = profileRepo.getStats(uid).first() // Get current stats
                if (result is UiResult.Success) {
                    val currentStats = result.data
                    if (currentStats != null) {
                        val updatedStats = currentStats.copy(points = currentStats.points + points)
                        profileRepo.upsertStats(updatedStats)
                    }
                }
            }
        }
    }

    fun deleteTask(task: TaskEntity) = viewModelScope.launch {
        taskRepo.deleteTask(task)
    }

    suspend fun getTask(id: String): UiResult<TaskEntity?> {
        return taskRepo.getTaskById(id)
    }
}

// Helper to convert UiResult to UiState
fun <T> UiResult<T>.toUiState(): UiState<T> {
    return when (this) {
        is UiResult.Success -> UiState.Success(this.data)
        is UiResult.Error -> UiState.Error(this.exception?.message ?: "Erro desconhecido")
        is UiResult.Loading -> UiState.Loading
    }
}
