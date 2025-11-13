package com.example.listagamificada.data.repository

import com.example.listagamificada.data.local.dao.StatsDao
import com.example.listagamificada.data.local.dao.TaskDao
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.local.entity.TaskEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val statsDao: StatsDao, 
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun getStats(uid: String): Flow<StatsEntity?> {
        return statsDao.getStatsFlow(uid)
    }

    fun getTasks(uid: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksFlow(uid)
    }

    suspend fun upsertStats(stats: StatsEntity) {
        statsDao.upsert(stats)
        firestore.collection("stats").document(stats.userId).set(stats).await()
    }

    // CORREÇÃO: Função agora retorna um Flow para ouvir mudanças em tempo real.
    fun getClaimedChallengeIds(userId: String): Flow<Set<String>> {
        return firestore.collection("stats").document(userId).collection("claimed_challenges")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { it.id }.toSet()
            }
    }

    suspend fun claimChallenge(userId: String, challengeId: String) {
        try {
            firestore.collection("stats").document(userId).collection("claimed_challenges").document(challengeId).set(mapOf("claimedAt" to System.currentTimeMillis())).await()
        } catch (_: Exception) {
            // Tratar erro se necessário
        }
    }
}