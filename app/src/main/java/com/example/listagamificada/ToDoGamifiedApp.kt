package com.example.listagamificada

import android.app.Application
import androidx.room.Room
import com.example.listagamificada.data.local.db.AppDatabase
import com.example.listagamificada.data.remote.retrofit.QuoteApi
import com.example.listagamificada.data.repository.AppRepository
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.QuoteRepository
import com.example.listagamificada.data.repository.TaskRepository
import com.example.listagamificada.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ToDoGamifiedApp : Application() {

    // Lazy initialization for dependencies
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration(true).build()
    }

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val quoteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://zenquotes.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApi::class.java)
    }

    // Repositories
    private val appRepository by lazy { AppRepository(database.taskDao(), database.statsDao(), database.rankingDao(), firestore) }
    private val quoteRepository by lazy { QuoteRepository(quoteApi, this) }
    private val profileRepository by lazy { ProfileRepository(database.statsDao()) }
    private val taskRepository by lazy { TaskRepository(database.taskDao()) } // Add TaskRepository

    // Auth
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    // ViewModel Factory
    val viewModelFactory by lazy {
        ViewModelFactory(appRepository, quoteRepository, profileRepository, taskRepository, firebaseAuth)
    }
}