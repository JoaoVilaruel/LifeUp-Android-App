package com.example.listagamificada.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatsEntity(
    @PrimaryKey
    val userId: String,
    val points: Int = 0,
    val badges: String = "",
    val lastClaimedDaily: Long = 0L // Timestamp of the last daily reward claim
)
