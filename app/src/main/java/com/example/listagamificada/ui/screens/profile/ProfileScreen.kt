package com.example.listagamificada.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.listagamificada.ui.navigation.Screen
import com.example.listagamificada.ui.screens.tasks.bottomNavItems
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    onLogout: () -> Unit,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        profileViewModel.loadProfileData()
    }

    val profileUiState by profileViewModel.uiState.collectAsState()
    val user = authViewModel.currentUser()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navController.navigate(screen.route) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // CORREÇÃO: Usando a cor de fundo do tema atual.
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (val state = profileUiState) {
                is UiState.Loading, is UiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Erro ao carregar perfil: ${state.message}", color = Color.Red)
                    }
                }
                is UiState.Success -> {
                    val profileData = state.data

                    UserInfoCard(
                        name = user?.displayName,
                        email = user?.email,
                        xpProgress = profileData.xpProgress,
                        currentXp = profileData.currentXp,
                        xpForNextLevel = profileData.xpForNextLevel
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    StatsDashboardCard(
                        level = profileData.stats?.level ?: 1,
                        completed = profileData.tasksCompletedCount,
                        pending = profileData.tasksPendingCount
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    ActivityChartsCard(favoriteCategory = profileData.favoriteCategory)

                    Spacer(modifier = Modifier.height(16.dp))
                    NotificationsCard()
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560).copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sair")
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoCard(name: String?, email: String?, xpProgress: Float, currentXp: Int, xpForNextLevel: Int) {
    val offWhite = Color(0xFFF0F0F0)
    val cyberPurple = Color(0xFF9f5fde)
    val brightPink = Color(0xFFE94560)

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E).copy(alpha = 0.5f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(cyberPurple, brightPink))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name?.firstOrNull()?.uppercase() ?: "U", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = offWhite)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(name ?: "Usuário", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = offWhite)
                    Text(email ?: "", fontSize = 14.sp, color = offWhite.copy(alpha = 0.7f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("XP: $currentXp / $xpForNextLevel", fontSize = 12.sp, color = offWhite.copy(alpha = 0.7f))
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = xpProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = cyberPurple,
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun StatsDashboardCard(level: Int, completed: Int, pending: Int) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E).copy(alpha = 0.5f))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(icon = Icons.Default.Star, value = level.toString(), label = "Nível")
            StatItem(icon = Icons.Default.CheckCircle, value = completed.toString(), label = "Concluídas")
            StatItem(icon = Icons.Default.Warning, value = pending.toString(), label = "Pendentes")
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color(0xFF9f5fde), modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun ActivityChartsCard(favoriteCategory: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E).copy(alpha = 0.5f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Categorias Favoritas", fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(favoriteCategory, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun NotificationsCard() {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E).copy(alpha = 0.5f))) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Ative as notificações para receber lembretes e acompanhar seu progresso", textAlign = TextAlign.Center, color = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560))) {
                Text("Ativar Notificações")
            }
        }
    }
}