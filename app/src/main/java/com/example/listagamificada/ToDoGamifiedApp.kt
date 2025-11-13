package com.example.listagamificada

import android.app.Application
import android.content.Context
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
    private val database by lazy { AppDatabase.getDatabase(applicationContext) }

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val sharedPreferences by lazy {
        getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
    }

    private val quoteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://zenquotes.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApi::class.java)
    }

    // Auth
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Repositories
    private val appRepository by lazy { AppRepository(database.taskDao(), database.statsDao(), database.rankingDao(), firestore, firebaseAuth) }
    private val quoteRepository by lazy { QuoteRepository(quoteApi) }
    // CORREÇÃO: Passando as dependências do Firebase para o ProfileRepository
    private val profileRepository by lazy { ProfileRepository(database.statsDao(), database.taskDao(), firestore, firebaseAuth) }
    private val taskRepository by lazy { TaskRepository(database.taskDao()) }

    // ViewModel Factory
    val viewModelFactory by lazy {
        ViewModelFactory(appRepository, profileRepository, taskRepository, quoteRepository, sharedPreferences, firebaseAuth)
    }
}