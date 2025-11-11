// Define o pacote para o tema da interface do usuário.
package com.example.listagamificada.ui.theme

// Importa as classes necessárias do Android e do Jetpack Compose.
import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Tema escuro padrão (Cyberpunk).
private val DefaultDarkColorScheme = darkColorScheme(
    primary = CyberPurple,
    secondary = BrightAqua,
    tertiary = NeonPink,
    background = DarkCharcoal,
    surface = NavyBlue,
    onPrimary = OffWhite,
    onSecondary = DarkCharcoal,
    onTertiary = OffWhite,
    onBackground = OffWhite,
    onSurface = OffWhite
)

// Novo tema "Cyber" (comprável).
private val CyberThemeColorScheme = darkColorScheme(
    primary = NeonPink,          // Cor primária mais agressiva.
    secondary = BrightAqua,
    tertiary = CyberPurple,
    background = Color(0xFF0A0A1A), // Fundo ainda mais escuro.
    surface = Color(0xFF1A1A2E),
    onPrimary = DarkCharcoal,
    onSecondary = DarkCharcoal,
    onTertiary = OffWhite,
    onBackground = OffWhite,
    onSurface = OffWhite
)

// Retorna o esquema de cores com base no ID do tema.
fun getTheme(themeId: String): ColorScheme {
    return when (themeId) {
        "cyber" -> CyberThemeColorScheme
        else -> DefaultDarkColorScheme
    }
}

// Composable que aplica o tema da aplicação.
@Composable
fun ListaGamificadaTheme(
    themeId: String = "default", // ID do tema a ser usado, padrão é "default".
    content: @Composable () -> Unit // Conteúdo da UI que usará o tema.
) {
    // Obtém o esquema de cores com base no ID do tema.
    val colorScheme = getTheme(themeId)

    // Obtém a visualização atual.
    val view = LocalView.current
    if (!view.isInEditMode) {
        // Efeito colateral para alterar a aparência da barra de status do sistema.
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Define a cor da barra de status.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Define os ícones da barra de status para escuros.
        }
    }

    // Aplica o MaterialTheme com o esquema de cores e a tipografia definidos.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
