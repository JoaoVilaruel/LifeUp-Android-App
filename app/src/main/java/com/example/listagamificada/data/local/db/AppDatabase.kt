package com.example.listagamificada.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.listagamificada.data.local.dao.TaskDao
import com.example.listagamificada.data.local.dao.StatsDao
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.data.local.entity.StatsEntity

@Database(entities = [TaskEntity::class, StatsEntity::class], version = 4) // <-- Version updated from 3 to 4
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun statsDao(): StatsDao
}
