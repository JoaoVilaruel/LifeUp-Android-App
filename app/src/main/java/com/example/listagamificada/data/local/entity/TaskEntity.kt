package com.example.listagamificada.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val ownerId: String,
    val completed: Boolean = false,
    val difficulty: String = "Fácil",
    val category: String = "Pessoal",
    val dueDate: Long? = null
)
