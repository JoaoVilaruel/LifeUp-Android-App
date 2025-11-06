package com.example.listagamificada.data.repository

import com.example.listagamificada.data.local.dao.TaskDao
import com.example.listagamificada.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {

    fun getTasks(uid: String): Flow<UiResult<List<TaskEntity>>> {
        return taskDao.getTasksFlow(uid)
            .map { UiResult.Success(it) as UiResult<List<TaskEntity>> }
            .catch { e -> emit(UiResult.Error(e)) }
    }

    suspend fun addTask(task: TaskEntity): UiResult<Unit> {
        return try {
            taskDao.insert(task)
            UiResult.Success(Unit)
        } catch (e: Exception) {
            UiResult.Error(e)
        }
    }

    suspend fun updateTask(task: TaskEntity): UiResult<Unit> {
        return try {
            taskDao.update(task)
            UiResult.Success(Unit)
        } catch (e: Exception) {
            UiResult.Error(e)
        }
    }

    suspend fun deleteTask(task: TaskEntity): UiResult<Unit> {
        return try {
            taskDao.delete(task)
            UiResult.Success(Unit)
        } catch (e: Exception) {
            UiResult.Error(e)
        }
    }

    suspend fun getTaskById(id: String): UiResult<TaskEntity?> {
        return try {
            val t = taskDao.getById(id)
            UiResult.Success(t)
        } catch (e: Exception) {
            UiResult.Error(e)
        }
    }
}
