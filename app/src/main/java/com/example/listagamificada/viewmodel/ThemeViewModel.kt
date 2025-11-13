package com.example.listagamificada.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// Enum para representar os temas disponíveis
enum class Theme {
    LIGHT, DEFAULT, DARK
}

class ThemeViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val _theme = mutableStateOf(getInitialTheme())
    val theme: State<Theme> = _theme

    companion object {
        private const val THEME_KEY = "app_theme"
    }

    private fun getInitialTheme(): Theme {
        val themeName = sharedPreferences.getString(THEME_KEY, Theme.DEFAULT.name)
        return Theme.valueOf(themeName ?: Theme.DEFAULT.name)
    }

    fun setTheme(theme: Theme) {
        _theme.value = theme
        sharedPreferences.edit().putString(THEME_KEY, theme.name).apply()
    }
}