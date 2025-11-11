package com.example.listagamificada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.listagamificada.ui.navigation.NavGraph
import com.example.listagamificada.ui.theme.ListaGamificadaTheme
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the unified factory from the Application class
        val factory = (application as ToDoGamifiedApp).viewModelFactory

        // Get the MainViewModel instance using the unified factory
        val mainViewModel: MainViewModel by viewModels { factory }

        setContent {
            // Observe the stats state to get the current theme
            val statsState by mainViewModel.stats.collectAsState()
            val equippedTheme = (statsState as? UiState.Success)?.data?.equippedTheme ?: "default"

            ListaGamificadaTheme(themeId = equippedTheme) {
                // Pass the unified factory to the NavGraph
                NavGraph(factory = factory)
            }
        }
    }
}
