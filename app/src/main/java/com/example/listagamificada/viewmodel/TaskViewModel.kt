package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.TaskRepository
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
                _tasks.value = result
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
            val xpGained = when (task.difficulty) {
                "Fácil" -> 10
                "Médio" -> 25
                "Difícil" -> 50
                else -> 0
            }

            val uid = getUserId()
            if (uid != null && xpGained > 0) {
                val currentStats = profileRepo.getStats(uid).first()
                if (currentStats != null) {
                    var newXp = currentStats.xp + xpGained
                    var newLevel = currentStats.level
                    var xpForNextLevel = newLevel * 100

                    while (newXp >= xpForNextLevel) {
                        newXp -= xpForNextLevel
                        newLevel++
                        xpForNextLevel = newLevel * 100
                    }

                    val updatedStats = currentStats.copy(
                        points = currentStats.points + xpGained, // Also update points
                        xp = newXp,
                        level = newLevel
                    )
                    profileRepo.upsertStats(updatedStats)
                }
            }
        }
    }

    fun deleteTask(task: TaskEntity) = viewModelScope.launch {
        taskRepo.deleteTask(task)
    }

    suspend fun getTask(id: String): TaskEntity? {
        return when (val result = taskRepo.getTaskById(id)) {
            is UiState.Success -> result.data
            else -> null
        }
    }
}