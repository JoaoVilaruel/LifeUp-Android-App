// Define o pacote para as telas de citação.
package com.example.listagamificada.ui.screens.quotes

// Importações de bibliotecas do Jetpack Compose e outras dependências.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.data.remote.retrofit.QuoteResponse
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.QuoteViewModel

// Composable para a tela que exibe uma citação aleatória.
@Composable
fun QuoteScreen(factory: ViewModelProvider.Factory) {
    // Obtém a instância do QuoteViewModel.
    val viewModel: QuoteViewModel = viewModel(factory = factory)
    // Coleta o estado da citação do ViewModel.
    val quoteState by viewModel.quoteState.collectAsState()

    // Cores do tema.
    val neonPink = Color(0xFFE94560)

    // Efeito para buscar uma citação aleatória quando a tela é iniciada.
    LaunchedEffect(Unit) {
        viewModel.fetchRandomQuote()
    }

    // Layout principal da tela.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Gerencia a exibição da UI com base no estado do carregamento dos dados.
        when (val state = quoteState) {
            is UiState.Loading -> CircularProgressIndicator(color = neonPink)
            is UiState.Error -> Text("Falha ao buscar frase: ${state.message}", color = neonPink)
            is UiState.Success -> {
                // Exibe o conteúdo da citação se o carregamento for bem-sucedido.
                QuoteContent(quote = state.data, onRefresh = { viewModel.fetchRandomQuote() })
            }
            is UiState.Idle -> {} // Estado inicial ou ocioso.
        }
    }
}

// Composable privado para exibir o conteúdo da citação.
@Composable
private fun QuoteContent(quote: QuoteResponse, onRefresh: () -> Unit) {
    // Cores do tema.
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Box com fundo em gradiente para destacar a citação.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(navyBlue.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
                .padding(32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Texto da citação.
                Text(
                    text = "\"${quote.q}\"",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = offWhite,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Autor da citação.
                Text(
                    text = "- ${quote.a}",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    color = offWhite.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Botão para buscar uma nova citação.
        Button(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth(0.8f).height(52.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            // Estilo do botão com gradiente.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(cyberPurple, neonPink)
                        )
                    )
                    .clip(RoundedCornerShape(100)),
                contentAlignment = Alignment.Center
            ) {
                Text("NOVA FRASE", color = offWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}
