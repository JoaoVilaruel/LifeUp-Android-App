package com.example.listagamificada.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listagamificada.data.repository.AppRepository
import com.example.listagamificada.data.repository.ProfileRepository
import com.example.listagamificada.data.repository.QuoteRepository
import com.example.listagamificada.di.DI
import com.google.firebase.auth.FirebaseAuth

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(
    private val appRepository: AppRepository,
    private val profileRepo: ProfileRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    private val quoteRepository by lazy { QuoteRepository(DI.quoteApi, context) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(appRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(profileRepo, FirebaseAuth.getInstance()) as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(appRepository) as T
            modelClass.isAssignableFrom(QuoteViewModel::class.java) -> QuoteViewModel(quoteRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
