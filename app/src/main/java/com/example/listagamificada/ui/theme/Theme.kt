package com.example.listagamificada.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.listagamificada.viewmodel.Theme

// TEMA PADRÃO (O que já existia)
private val DefaultColorScheme = darkColorScheme(
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

// CORREÇÃO: Tema escuro agora usa preto e tons de cinza.
private val DarkThemeColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),       // Roxo suave como cor primária
    secondary = Color(0xFF03DAC6),     // Ciano como cor secundária
    tertiary = Color(0xFF03DAC6),      // Ciano como cor terciária
    background = Color.Black,          // Fundo preto
    surface = Color(0xFF121212),       // Superfícies em cinza escuro
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

// TEMA CLARO
private val LightThemeColorScheme = lightColorScheme(
    primary = CyberPurple,
    secondary = NavyBlue,
    tertiary = NeonPink,
    background = Color(0xFFF0F2F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)


@Composable
fun ListaGamificadaTheme(
    theme: Theme = Theme.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        Theme.LIGHT -> LightThemeColorScheme
        Theme.DARK -> DarkThemeColorScheme
        Theme.DEFAULT -> DefaultColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = theme == Theme.LIGHT
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}