package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listagamificada.data.repository.AppRepository
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.QuoteRepository
import com.example.listagamificada.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth

class ViewModelFactory(
    private val appRepository: AppRepository,
    private val quoteRepository: QuoteRepository,
    private val profileRepository: ProfileRepository,
    private val taskRepository: TaskRepository, // Add TaskRepository
    private val firebaseAuth: FirebaseAuth
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val authViewModel = AuthViewModel(appRepository, firebaseAuth) // Create instance to be shared

        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(appRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                authViewModel as T
            }
            modelClass.isAssignableFrom(QuoteViewModel::class.java) -> {
                QuoteViewModel(quoteRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(profileRepository, firebaseAuth) as T
            }
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
                TaskViewModel(taskRepository, profileRepository, authViewModel) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}