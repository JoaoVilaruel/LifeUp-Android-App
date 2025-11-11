package com.example.listagamificada.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single user's entry in the ranking table.
 */
@Entity(tableName = "ranking")
data class RankingEntity(
    @PrimaryKey
    val userId: String,
    val userName: String,
    val points: Int
)
