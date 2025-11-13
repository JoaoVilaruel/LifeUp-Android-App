package com.example.listagamificada.data.repository

import com.example.listagamificada.data.local.dao.TaskDao
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class TaskRepository(private val taskDao: TaskDao) {

    // Retorna um Flow com o estado da UI (usado pela TaskListScreen)
    fun getTasks(uid: String): Flow<UiState<List<TaskEntity>>> {
        return taskDao.getTasksFlow(uid)
            .map<List<TaskEntity>, UiState<List<TaskEntity>>> { UiState.Success(it) }
            .onStart { emit(UiState.Loading) }
            .catch { e -> emit(UiState.Error(e.message ?: "Erro ao carregar tarefas")) }
    }

    // CORREÇÃO: Nova função que retorna apenas os dados brutos (para o ChallengesViewModel).
    fun getRawTasks(uid: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksFlow(uid)
    }

    suspend fun addTask(task: TaskEntity): UiState<Unit> {
        return try {
            taskDao.insert(task)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Erro ao adicionar tarefa")
        }
    }

    suspend fun updateTask(task: TaskEntity): UiState<Unit> {
        return try {
            taskDao.update(task)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Erro ao atualizar tarefa")
        }
    }

    suspend fun deleteTask(task: TaskEntity): UiState<Unit> {
        return try {
            taskDao.delete(task)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Erro ao deletar tarefa")
        }
    }

    suspend fun getTaskById(id: String): UiState<TaskEntity?> {
        return try {
            val t = taskDao.getById(id)
            UiState.Success(t)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Erro ao buscar tarefa")
        }
    }
}