package com.example.listagamificada.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listagamificada.data.local.db.AppDatabase
import com.example.listagamificada.data.repository.AppRepository
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth

// CORREÇÃO: Este arquivo agora é um espelho da ViewModelFactory correta.
// Isso garante que qualquer parte do código que o utilize funcione.
class AppViewModelFactory(
    private val appRepository: AppRepository,
    private val profileRepository: ProfileRepository,
    private val taskRepository: TaskRepository,
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
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}