package com.example.listagamificada.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(factory: ViewModelProvider.Factory, onLogout: () -> Unit) {
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val statsState = profileViewModel.statsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val user = authViewModel.currentUser()
    val uid = profileViewModel.getUserId() ?: ""

    LaunchedEffect(Unit) {
        profileViewModel.rewardMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            profileViewModel.loadStats(uid)
        }
    }
    
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val s = statsState.value) {
                is UiState.Loading -> CircularProgressIndicator(color = neonPink)
                is UiState.Error -> Text("Erro: ${s.message}", color = neonPink)
                is UiState.Success -> {
                    val stats = s.data
                    val points = stats?.points ?: 0
                    val level = (points / 100) + 1
                    val xpForNextLevel = 100
                    val currentXp = points % xpForNextLevel
                    val progress = currentXp.toFloat() / xpForNextLevel

                    // --- Player Info Card ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(cyberPurple.copy(alpha = 0.3f), navyBlue)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person, 
                                contentDescription = "Avatar", 
                                modifier = Modifier.size(90.dp),
                                tint = offWhite
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                user?.email ?: "Jogador", 
                                fontSize = 22.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = offWhite
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Nível $level", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = neonPink)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.height(10.dp).fillMaxWidth().clip(CircleShape),
                                color = neonPink,
                                trackColor = navyBlue.copy(alpha = 0.5f)
                            )
                            Text("$currentXp / $xpForNextLevel XP", fontSize = 12.sp, color = offWhite.copy(alpha = 0.8f))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Badges/Achievements Section ---
                    Text(
                        "Conquistas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Start),
                        color = offWhite
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.Star, "Conquista 1", modifier = Modifier.size(40.dp), tint = Color.Gray)
                        Icon(Icons.Default.Star, "Conquista 2", modifier = Modifier.size(40.dp), tint = Color.Gray)
                        Icon(Icons.Default.Star, "Conquista 3", modifier = Modifier.size(40.dp), tint = Color.Gray)
                    }
                }
                is UiState.Idle -> {}
            }

            Spacer(modifier = Modifier.weight(1f)) // Pushes logout button to the bottom

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = neonPink.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sair", color = offWhite)
            }
        }
    }
}
