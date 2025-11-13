package com.example.listagamificada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.listagamificada.ui.navigation.NavGraph
import com.example.listagamificada.ui.theme.ListaGamificadaTheme
import com.example.listagamificada.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as ToDoGamifiedApp
        val factory = app.viewModelFactory

        // PASSO 3.1: Obtendo o ThemeViewModel
        val themeViewModel: ThemeViewModel by viewModels { factory }

        setContent {
            // PASSO 3.2: Passando o tema do ViewModel para o Composable do Tema
            ListaGamificadaTheme(theme = themeViewModel.theme.value) {
                NavGraph(factory = factory)
            }
        }
    }
}
