package com.example.listagamificada.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.QuoteRepository
import com.example.listagamificada.data.repository.TaskRepository
import com.example.listagamificada.di.DI

class AppViewModelFactory(
    private val taskRepo: TaskRepository,
    private val profileRepo: ProfileRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    private val authViewModel = AuthViewModel()
    private val quoteRepository by lazy { QuoteRepository(DI.quoteApi, context) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> TaskViewModel(taskRepo, profileRepo, authViewModel) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(profileRepo, authViewModel) as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> authViewModel as T
            modelClass.isAssignableFrom(QuoteViewModel::class.java) -> QuoteViewModel(quoteRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
