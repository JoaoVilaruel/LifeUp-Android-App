package com.example.listagamificada.ui.screens.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.ui.navigation.Screen
import com.example.listagamificada.ui.screens.tasks.bottomNavItems
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.MainViewModel
import com.example.listagamificada.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(factory: ViewModelFactory, navController: NavController) {
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val rankingState by mainViewModel.ranking.collectAsState()
    val currentUserId = authViewModel.getUserId()

    LaunchedEffect(currentUserId) {
        mainViewModel.loadRanking()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
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
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (val state = rankingState) {
                is UiState.Loading, is UiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFE94560))
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Erro ao carregar o ranking. Verifique se o índice composto (level, coins) foi criado no Firestore.", color = Color(0xFFE94560), textAlign = TextAlign.Center)
                    }
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nenhum jogador no ranking ainda.", color = Color.White.copy(alpha = 0.8f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(state.data) { index, playerStats ->
                                RankingItem(rank = index + 1, stats = playerStats, isCurrentUser = playerStats.userId == currentUserId)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingItem(rank: Int, stats: StatsEntity, isCurrentUser: Boolean) {
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700) // Ouro
        2 -> Color(0xFFC0C0C0) // Prata
        3 -> Color(0xFFCD7F32) // Bronze
        else -> null
    }

    val backgroundBrush = Brush.horizontalGradient(
        colors = listOf(navyBlue, cyberPurple.copy(alpha = 0.4f))
    )

    val borderBrush = if (isCurrentUser) Brush.horizontalGradient(listOf(neonPink, cyberPurple)) else null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundBrush)
            .then(
                if (borderBrush != null) Modifier.border(2.dp, borderBrush, RoundedCornerShape(16.dp)) else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#$rank",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = medalColor ?: offWhite
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Avatar",
                tint = offWhite,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(navyBlue.copy(alpha = 0.5f))
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isCurrentUser) "Você" else stats.userName.ifBlank { "Jogador Anônimo" },
                fontWeight = FontWeight.SemiBold,
                color = offWhite,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            medalColor?.let {
                // CORREÇÃO VISUAL FINAL:
                Icon(Icons.Filled.Person, contentDescription = "Medalha", tint = it)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "Nível ${stats.level} (${stats.coins} Pontos)",
                fontWeight = FontWeight.Bold,
                color = neonPink
            )
        }
    }
}