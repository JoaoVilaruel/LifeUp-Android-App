package com.example.listagamificada.data.repository

import com.example.listagamificada.data.local.dao.RankingDao
import com.example.listagamificada.data.local.dao.StatsDao
import com.example.listagamificada.data.local.dao.TaskDao
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.local.entity.TaskEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AppRepository(
    private val taskDao: TaskDao,
    private val statsDao: StatsDao,
    private val rankingDao: RankingDao,
    private val firestore: FirebaseFirestore
) {

    fun getTasks(uid: String): Flow<List<TaskEntity>> = taskDao.getTasksFlow(uid)
    suspend fun addTask(task: TaskEntity) = taskDao.insert(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.update(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.delete(task)
    suspend fun getTaskById(id: String): TaskEntity? = taskDao.getById(id)

    fun getStats(uid: String): Flow<StatsEntity?> = statsDao.getStatsFlow(uid)

    suspend fun upsertStats(stats: StatsEntity) {
        // Save to both local Room DB and remote Firestore
        statsDao.upsert(stats) // Local
        firestore.collection("stats").document(stats.userId).set(stats).await() // Remote
    }

    fun getRanking(): Flow<List<StatsEntity>> {
        val query = firestore.collection("stats")
            .orderBy("level", Query.Direction.DESCENDING)
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(50)

        return query.snapshots().map {
            it.toObjects(StatsEntity::class.java)
        }
    }
}