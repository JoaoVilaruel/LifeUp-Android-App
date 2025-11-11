// Define o pacote para a tela de splash.
package com.example.listagamificada.ui.screens.splash

// Importações de bibliotecas do Jetpack Compose e outras dependências.
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Composable para a tela de splash (tela de abertura).
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Estado para controlar o início da animação.
    var startAnimation by remember { mutableStateOf(false) }
    // Animação de fade-in (transparência) para os elementos da tela.
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f, // Anima de 0f (invisível) para 1f (visível).
        animationSpec = tween(durationMillis = 2000), // Duração da animação.
        label = "Splash Alpha"
    )

    // Efeito que é executado uma vez quando a tela é criada.
    LaunchedEffect(key1 = true) {
        startAnimation = true // Inicia a animação.
        delay(2500) // Aguarda um tempo total para a exibição da tela de splash.
        onTimeout() // Chama o callback para navegar para a próxima tela.
    }

    // Cores personalizadas do tema.
    val darkCharcoal = Color(0xFF1A1A2E)
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val offWhite = Color(0xFFF0F0F0)

    // Layout principal da tela com fundo em gradiente.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(darkCharcoal, navyBlue))),
        contentAlignment = Alignment.Center
    ) {
        // Coluna para organizar o logo e o nome da aplicação.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim.value) // Aplica a animação de transparência.
        ) {
            // Ícone da aplicação.
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp),
                tint = neonPink
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Nome da aplicação.
            Text(
                text = "LifeUp",
                style = TextStyle(
                    color = offWhite,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
