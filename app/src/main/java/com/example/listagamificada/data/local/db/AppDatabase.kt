package com.example.listagamificada.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.listagamificada.data.local.dao.RankingDao
import com.example.listagamificada.data.local.dao.StatsDao
import com.example.listagamificada.data.local.dao.TaskDao
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.model.entity.RankingEntity

@Database(entities = [TaskEntity::class, StatsEntity::class, RankingEntity::class], version = 10, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun statsDao(): StatsDao
    abstract fun rankingDao(): RankingDao

    // CORREÇÃO: Adicionando o companion object para criar a instância do DB
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamified_list_database"
                )
                .addMigrations(MIGRATION_9_10) // Adicionando a migração que já existe no arquivo
                .fallbackToDestructiveMigration() // Segurança para evitar crashes por migração
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tasks ADD COLUMN category TEXT NOT NULL DEFAULT 'Pessoal'")
        database.execSQL("ALTER TABLE tasks ADD COLUMN dueDate INTEGER")
    }
}