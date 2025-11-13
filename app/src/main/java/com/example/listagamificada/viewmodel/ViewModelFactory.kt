package com.example.listagamificada.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listagamificada.data.repository.*
import com.google.firebase.auth.FirebaseAuth

class ViewModelFactory(
    private val appRepository: AppRepository,
    private val profileRepository: ProfileRepository,
    private val taskRepository: TaskRepository,
    private val quoteRepository: QuoteRepository,
    private val sharedPreferences: SharedPreferences,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(appRepository) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(sharedPreferences) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(appRepository) as T
            }
            modelClass.isAssignableFrom(ChallengesViewModel::class.java) -> {
                val authViewModel = AuthViewModel(appRepository)
                ChallengesViewModel(profileRepository, taskRepository, authViewModel) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(profileRepository, auth) as T
            }
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
                val authViewModel = AuthViewModel(appRepository)
                TaskViewModel(taskRepository, profileRepository, authViewModel) as T
            }
            modelClass.isAssignableFrom(QuoteViewModel::class.java) -> {
                QuoteViewModel(quoteRepository) as T
            }
            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> {
                ThemeViewModel(sharedPreferences) as T
            }
            // CORREÇÃO: Lógica de criação do ShopViewModel removida.
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}