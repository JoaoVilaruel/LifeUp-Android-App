package com.example.listagamificada.data.repository

import com.example.listagamificada.data.local.entity.TaskEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val db: FirebaseFirestore) {

    fun getTasks(uid: String): Flow<UiResult<List<TaskEntity>>> = flow {
        try {
            val snapshot = db.collection("users").document(uid).collection("tasks").get().await()
            val tasks = snapshot.toObjects<TaskEntity>()
            emit(UiResult.Success(tasks))
        } catch (e: Exception) {
            emit(UiResult.Error(e))
        }
    }

    fun getTask(uid: String, id: String): Flow<UiResult<TaskEntity?>> = flow {
        try {
            val doc = db.collection("users").document(uid).collection("tasks").document(id).get().await()
            val task = doc.toObject(TaskEntity::class.java)
            emit(UiResult.Success(task))
        } catch (e: Exception) {
            emit(UiResult.Error(e))
        }
    }

    suspend fun addTask(uid: String, task: TaskEntity) {
        db.collection("users").document(uid).collection("tasks").document(task.id).set(task).await()
    }

    suspend fun updateTask(uid: String, task: TaskEntity) {
        db.collection("users").document(uid).collection("tasks").document(task.id).set(task).await()
    }

    suspend fun deleteTask(uid: String, task: TaskEntity) {
        db.collection("users").document(uid).collection("tasks").document(task.id).delete().await()
    }
}
