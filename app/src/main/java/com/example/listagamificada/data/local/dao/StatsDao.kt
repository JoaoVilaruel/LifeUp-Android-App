package com.example.listagamificada.data.local.dao

import androidx.room.*
import com.example.listagamificada.data.local.entity.StatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    @Query("SELECT * FROM stats WHERE userId = :uid LIMIT 1")
    fun getStatsFlow(uid: String): Flow<StatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: StatsEntity)

    @Query("DELETE FROM stats WHERE userId = :uid")
    suspend fun deleteByUser(uid: String)
}
