package com.example.listagamificada.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class OnboardingViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    companion object {
        const val ONBOARDING_COMPLETED_KEY = "onboarding_completed"
    }

    fun hasOnboardingBeenShown(): Boolean {
        return sharedPreferences.getBoolean(ONBOARDING_COMPLETED_KEY, false)
    }

    fun setOnboardingShown() {
        sharedPreferences.edit().putBoolean(ONBOARDING_COMPLETED_KEY, true).apply()
    }

    fun resetOnboardingShown() {
        sharedPreferences.edit().putBoolean(ONBOARDING_COMPLETED_KEY, false).apply()
    }
}