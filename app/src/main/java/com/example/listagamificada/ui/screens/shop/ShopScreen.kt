// Define o pacote para as telas da loja.
package com.example.listagamificada.ui.screens.shop

// Importações de bibliotecas do Jetpack Compose e outras dependências.
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.MainViewModel
import com.example.listagamificada.viewmodel.ProfileViewModel

// Estrutura de dados para representar um item da loja.
data class ShopItem(val id: String, val name: String, val price: Int, val icon: ImageVector)

// Composable para a tela da loja.
@Composable
fun ShopScreen(factory: ViewModelProvider.Factory) {
    // Obtém instâncias dos ViewModels necessários.
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory) // Adiciona o AuthViewModel.
    // Coleta o estado das estatísticas do usuário.
    val statsState by mainViewModel.stats.collectAsState()
    // Obtém o ID do usuário logado.
    val userId = authViewModel.getUserId()

    // Efeito para carregar as estatísticas do usuário quando a tela é iniciada.
    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            mainViewModel.loadStatsForUser(userId)
        }
    }

    // Lista de itens disponíveis na loja.
    val items = listOf(
        ShopItem("cyber", "Tema Cyber", 50, Icons.Default.Build),
        ShopItem("ghost", "Avatar Fantasma", 100, Icons.Default.Face),
        ShopItem("legend", "Badge Lendário", 200, Icons.Default.Star)
    )

    // Gerencia a exibição da UI com base no estado do carregamento dos dados.
    when (val state = statsState) {
        is UiState.Loading, is UiState.Idle -> {
            // Exibe um indicador de progresso enquanto os dados estão sendo carregados.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            // Se o carregamento for bem-sucedido, exibe a loja.
            val userCoins = state.data?.coins ?: 0
            Column {
                // Card com a quantidade de moedas do usuário.
                UserCoinsCard(coins = userCoins)
                // Grade de itens da loja.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Duas colunas.
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items) { item ->
                        // Card para cada item da loja.
                        ShopItemCard(item = item, onPurchase = {
                            profileViewModel.purchaseItem(item)
                        })
                    }
                }
            }
        }
        is UiState.Error -> {
            // Exibe uma mensagem de erro se o carregamento falhar.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Erro ao carregar dados da loja.", color = Color.Red)
            }
        }
    }
}

// Composable para exibir a quantidade de moedas do usuário.
@Composable
fun UserCoinsCard(coins: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Coins", tint = Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$coins Coins", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}

// Composable para exibir um item individual da loja.
@Composable
fun ShopItemCard(item: ShopItem, onPurchase: () -> Unit) {
    // Cores do tema.
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val offWhite = Color(0xFFF0F0F0)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.border(1.dp, color = navyBlue, shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone do item.
            Icon(imageVector = item.icon, contentDescription = item.name, modifier = Modifier.size(50.dp), tint = neonPink)
            Spacer(modifier = Modifier.height(8.dp))
            // Nome do item.
            Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = offWhite)
            Spacer(modifier = Modifier.height(4.dp))
            // Preço do item.
            Row {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = neonPink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${item.price}", fontWeight = FontWeight.SemiBold, color = Color(0xFFFFD700))
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Botão de compra.
            Button(onClick = onPurchase) {
                Text("Comprar")
            }
        }
    }
}