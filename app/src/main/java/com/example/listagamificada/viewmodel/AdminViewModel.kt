package com.example.listagamificada.viewmodel

import androidx.lifecycle.ViewModel
import com.example.listagamificada.data.repository.AppRepository

/**
 * Manages UI-related data for the administration screens.
 * This will be used for features like editing ranking, managing users, etc.
 */
class AdminViewModel(private val repository: AppRepository) : ViewModel() {
    // Admin-specific logic will be added here in the future.
}
