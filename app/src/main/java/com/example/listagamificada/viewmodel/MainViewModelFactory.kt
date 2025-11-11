package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listagamificada.data.repository.AppRepository
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.QuoteRepository
import com.google.firebase.auth.FirebaseAuth

/**
 * A unified factory for creating all ViewModels in the application.
 * This ensures that each ViewModel receives its correct dependencies (repositories, etc.).
 */
class MainViewModelFactory(
    private val appRepository: AppRepository,
    private val quoteRepository: QuoteRepository,
    private val profileRepository: ProfileRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(appRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(appRepository, firebaseAuth) as T
            }
            modelClass.isAssignableFrom(QuoteViewModel::class.java) -> {
                QuoteViewModel(quoteRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(profileRepository, firebaseAuth) as T
            }
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(appRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}