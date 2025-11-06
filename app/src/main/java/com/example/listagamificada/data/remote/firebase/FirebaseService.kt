package com.example.listagamificada.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseService(
    private val db: FirebaseFirestore
) {
    suspend fun updateUserPoints(uid: String, points: Int): Result<Boolean> {
        return try {
            db.collection("users").document(uid).update("points", points).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLeaderboard(): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            Result.success(snapshot.documents.mapNotNull { it.data })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
