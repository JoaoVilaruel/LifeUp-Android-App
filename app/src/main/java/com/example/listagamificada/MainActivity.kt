package com.example.listagamificada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.example.listagamificada.data.local.db.AppDatabase
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.TaskRepository
import com.example.listagamificada.ui.navigation.navGraph
import com.example.listagamificada.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room DB
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "todogamified-db"
        ).fallbackToDestructiveMigration().build()

        // Repositories
        val taskRepo = TaskRepository(db.taskDao())
        val profileRepo = ProfileRepository(db.statsDao())

        // ViewModel factory
        val factory = AppViewModelFactory(taskRepo, profileRepo, applicationContext)

        setContent {
            navGraph(factory = factory)
        }
    }
}
