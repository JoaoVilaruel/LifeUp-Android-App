package com.example.listagamificada.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(factory: ViewModelFactory, onNavigateBack: () -> Unit) {
    val viewModel: AuthViewModel = viewModel(factory = factory)
    val user = viewModel.currentUser()
    
    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val darkCharcoal = Color(0xFF1A1A2E)
    val offWhite = Color(0xFFF0F0F0)
    val neonPink = Color(0xFFE94560)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkCharcoal)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Nome de Exibição") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = neonPink,
                    unfocusedBorderColor = offWhite.copy(alpha = 0.5f),
                    focusedLabelColor = neonPink,
                    unfocusedLabelColor = offWhite.copy(alpha = 0.7f),
                    cursorColor = neonPink,
                    focusedTextColor = offWhite,
                    unfocusedTextColor = offWhite
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val success = viewModel.updateDisplayName(displayName)
                        if (success) {
                            snackbarHostState.showSnackbar("Perfil atualizado com sucesso!")
                            onNavigateBack()
                        } else {
                            snackbarHostState.showSnackbar("Falha ao atualizar o perfil.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = neonPink)
            ) {
                Text("Salvar Alterações", fontWeight = FontWeight.Bold)
            }
        }
    }
}