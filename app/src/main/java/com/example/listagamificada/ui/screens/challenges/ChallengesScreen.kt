package com.example.listagamificada.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.listagamificada.ui.navigation.Screen
import com.example.listagamificada.ui.screens.tasks.bottomNavItems
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.Challenge
import com.example.listagamificada.viewmodel.ChallengesViewModel
import com.example.listagamificada.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(factory: ViewModelFactory, navController: NavController) { 
    val viewModel: ChallengesViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    val offWhite = Color(0xFFF0F0F0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Screen.Challenges.label) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
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
        // CORREÇÃO: Usando a cor de fundo do tema atual.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UiState.Loading, is UiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Erro: ${state.message}", color = Color.Red)
                    }
                }
                is UiState.Success -> {
                    val challengesData = state.data
                    val toCompleteChallenges = challengesData.challenges.filter { it.currentProgress < it.targetProgress }
                    val completedChallenges = challengesData.challenges.filter { it.currentProgress >= it.targetProgress && !it.isClaimed }
                    val claimedChallenges = challengesData.challenges.filter { it.isClaimed }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                    ) {
                        item {
                            Column {
                                Text("Missões Diárias", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = offWhite)
                                Text("Complete missões para ganhar moedas e comprar itens exclusivos", fontSize = 14.sp, color = offWhite.copy(alpha = 0.7f))
                            }
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                SummaryCard(modifier = Modifier.weight(1f), icon = Icons.Default.Star, value = challengesData.userCoins.toString(), label = "Moedas", color = Color(0xFFE6A919))
                                SummaryCard(modifier = Modifier.weight(1f), icon = Icons.Default.CheckCircle, value = "${challengesData.completedCount}/${challengesData.challenges.size}", label = "Completas", color = Color(0xFF9f5fde))
                            }
                        }

                        if (completedChallenges.isNotEmpty()) {
                            item {
                                Text("Prontas para Resgatar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = offWhite, modifier = Modifier.padding(top = 8.dp))
                            }
                            items(completedChallenges) { challenge ->
                                ChallengeItem(challenge = challenge, onClaimClick = viewModel::claimReward)
                            }
                        }

                        if (toCompleteChallenges.isNotEmpty()) {
                            item {
                                Text("Missões de Hoje", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = offWhite, modifier = Modifier.padding(top = 8.dp))
                            }
                            items(toCompleteChallenges) { challenge ->
                                ChallengeItem(challenge = challenge, onClaimClick = { /* No action needed */ })
                            }
                        }

                        if (claimedChallenges.isNotEmpty()) {
                            item {
                                Text("Reivindicadas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = offWhite, modifier = Modifier.padding(top = 8.dp))
                            }
                            items(claimedChallenges) { challenge ->
                                ChallengeItem(challenge = challenge, onClaimClick = { /* No action needed */ })
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = offWhite.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("As missões resetam diariamente à meia-noite", fontSize = 12.sp, color = offWhite.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
            Text(text = label, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun ChallengeItem(challenge: Challenge, onClaimClick: (String) -> Unit) {
    val progress = (challenge.currentProgress.toFloat() / challenge.targetProgress.toFloat()).coerceIn(0f, 1f)
    val isCompleted = progress >= 1f
    val canClaim = isCompleted && !challenge.isClaimed
    val offWhite = Color(0xFFF0F0F0)
    val cyberPurple = Color(0xFF9f5fde)
    val cardAlpha = if (challenge.isClaimed) 0.6f else 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E).copy(alpha = 0.5f * cardAlpha)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = challenge.icon,
                    contentDescription = challenge.title,
                    modifier = Modifier.size(32.dp),
                    tint = if (challenge.isClaimed) Color.Gray else cyberPurple
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = challenge.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = offWhite.copy(alpha = cardAlpha))
                    Text(text = challenge.description, fontSize = 14.sp, color = offWhite.copy(alpha = 0.7f * cardAlpha))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE6A919).copy(alpha = 0.15f * cardAlpha))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Moedas", tint = Color(0xFFE6A919).copy(alpha = cardAlpha), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = challenge.coinReward.toString(), color = Color(0xFFE6A919).copy(alpha = cardAlpha), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (!challenge.isClaimed) {
                Column {
                    Text(
                        text = "${challenge.currentProgress}/${challenge.targetProgress}",
                        fontSize = 12.sp,
                        color = offWhite.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = cyberPurple,
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )

                    if (canClaim) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onClaimClick(challenge.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825))
                        ) {
                            Text("Resgatar Recompensa", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Check, contentDescription = "Completo", tint = Color.Green, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Recompensa Resgatada", fontSize = 14.sp, color = Color.Green)
                }
            }
        }
    }
}
