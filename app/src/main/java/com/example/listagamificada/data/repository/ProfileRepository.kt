package com.example.listagamificada.data.repository

import com.example.listagamificada.data.local.dao.StatsDao
import com.example.listagamificada.data.local.entity.StatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ProfileRepository(private val statsDao: StatsDao) {

    fun getStats(uid: String): Flow<UiResult<StatsEntity?>> {
        return statsDao.getStatsFlow(uid)
            .map { UiResult.Success(it) as UiResult<StatsEntity?> }
            .catch { e -> emit(UiResult.Error(e)) }
    }

    suspend fun upsertStats(stats: StatsEntity) {
        statsDao.upsert(stats)
    }
}
