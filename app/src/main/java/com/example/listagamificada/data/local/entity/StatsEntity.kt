package com.example.listagamificada.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatsEntity(
    @PrimaryKey val userId: String = "",
    val userName: String = "",
    val points: Int = 0,
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 0, // Add coins property for shop currency
    val lastClaimedDaily: Long = 0L,
    val equippedTheme: String = "default",
    val unlockedThemes: String = "default"
)
