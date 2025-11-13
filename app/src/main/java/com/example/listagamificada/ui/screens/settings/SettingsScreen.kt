package com.example.listagamificada.ui.screens.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.listagamificada.ui.navigation.Screen
import com.example.listagamificada.viewmodel.Theme
import com.example.listagamificada.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(factory: ViewModelProvider.Factory, onNavigateBack: () -> Unit, navController: NavController) {
    // CORREÇÃO: Obtendo o ViewModel que está no escopo da Activity, o mesmo da MainActivity.
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(viewModelStoreOwner = context as ComponentActivity, factory = factory)
    
    val darkCharcoal = Color(0xFF1A1A2E)
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionTitle("Conta")
            Spacer(modifier = Modifier.height(8.dp))
            SettingItem(
                title = "Editar Perfil", 
                icon = Icons.Default.Edit, 
                onClick = { navController.navigate(Screen.EditProfile.route) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SectionTitle("Aparência")
            Spacer(modifier = Modifier.height(8.dp))
            ThemeSelector(themeViewModel = themeViewModel)

            Spacer(modifier = Modifier.weight(1f))

            // CORREÇÃO: Botão agora controla a exibição do diálogo.
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Excluir Conta")
            }
        }
    }

    // Diálogo de confirmação para exclusão de conta
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Conta") },
            text = { Text("Você tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = { 
                        // TODO: Implementar a lógica de exclusão real
                        showDeleteDialog = false 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.7f),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF16213E).copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF9f5fde))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelector(themeViewModel: ThemeViewModel) {
    val currentTheme = themeViewModel.theme.value
    val themes = listOf(Theme.LIGHT, Theme.DEFAULT, Theme.DARK)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF16213E).copy(alpha = 0.5f))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Star, contentDescription = "Tema", tint = Color(0xFF9f5fde))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Mudar Tema", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            themes.forEach {
                FilterChip(
                    selected = it == currentTheme,
                    onClick = { themeViewModel.setTheme(it) },
                    label = { Text(it.name.lowercase().replaceFirstChar { char -> char.uppercase() }) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}