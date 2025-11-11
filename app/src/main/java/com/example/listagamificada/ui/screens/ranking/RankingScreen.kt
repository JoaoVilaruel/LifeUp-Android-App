// Define o pacote para as telas de ranking.
package com.example.listagamificada.ui.screens.ranking

// Importações de bibliotecas do Jetpack Compose e outras dependências.
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.MainViewModel
import com.example.listagamificada.viewmodel.ViewModelFactory

// Composable para a tela de ranking.
@Composable
fun RankingScreen(factory: ViewModelFactory) {
    // Obtém instâncias dos ViewModels.
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    // Coleta o estado do ranking.
    val rankingState by mainViewModel.ranking.collectAsState()
    // Obtém o ID do usuário atual.
    val currentUserId = authViewModel.getUserId()

    // Efeito para carregar o ranking quando a tela é iniciada.
    LaunchedEffect(Unit) {
        mainViewModel.loadRanking()
    }

    // Gerencia a exibição da UI com base no estado do carregamento dos dados.
    when (val state = rankingState) {
        is UiState.Loading, is UiState.Idle -> {
            // Exibe um indicador de progresso durante o carregamento.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE94560))
            }
        }
        is UiState.Error -> {
            // Exibe uma mensagem de erro se o carregamento falhar.
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Erro: ${state.message}", color = Color(0xFFE94560), textAlign = TextAlign.Center)
            }
        }
        is UiState.Success -> {
            // Exibe a lista de jogadores no ranking.
            if (state.data.isEmpty()) {
                // Mensagem para quando o ranking está vazio.
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum jogador no ranking ainda.", color = Color.White.copy(alpha = 0.8f))
                }
            } else {
                // Lista rolável com os itens do ranking.
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

// Composable para cada item da lista de ranking.
@Composable
fun RankingItem(rank: Int, stats: StatsEntity, isCurrentUser: Boolean) {
    // Cores do tema.
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    // Cor da medalha de acordo com a posição no ranking.
    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700) // Ouro
        2 -> Color(0xFFC0C0C0) // Prata
        3 -> Color(0xFFCD7F32) // Bronze
        else -> null
    }

    // Gradiente de fundo para o item.
    val backgroundBrush = Brush.horizontalGradient(
        colors = listOf(navyBlue, cyberPurple.copy(alpha = 0.4f))
    )

    // Borda destacada para o usuário atual.
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
            // Posição no ranking.
            Text(
                text = "#$rank",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = medalColor ?: offWhite
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Ícone do jogador.
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
            // Nome do jogador.
            Text(
                text = if(isCurrentUser) "Você" else stats.userName,
                fontWeight = FontWeight.SemiBold,
                color = offWhite,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            // Ícone da medalha (se houver).
            medalColor?.let {
                Icon(Icons.Default.Person, contentDescription = "Medal", tint = it)
                Spacer(modifier = Modifier.width(8.dp))
            }
            // Nível e XP do jogador.
            Text(
                text = "Nível ${stats.level} (${stats.xp} XP)",
                fontWeight = FontWeight.Bold,
                color = neonPink
            )
        }
    }
}
