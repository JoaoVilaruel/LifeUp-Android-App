package com.example.listagamificada.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyberPurple,       // A cor principal, para botões e acentos
    secondary = BrightAqua,      // Uma cor secundária vibrante
    tertiary = NeonPink,         // Uma terceira cor para destaque
    background = DarkCharcoal,   // Fundo principal bem escuro
    surface = NavyBlue,          // Cor de superfícies como cards e barras
    onPrimary = OffWhite,
    onSecondary = DarkCharcoal,
    onTertiary = OffWhite,
    onBackground = OffWhite,
    onSurface = OffWhite
)

private val LightColorScheme = lightColorScheme(
    primary = NeonPink,
    secondary = ElectricBlue,
    tertiary = NeonPink,
    background = Color(0xFFF8F8FF), // Um branco levemente azulado
    surface = Color.White,
    onPrimary = OffWhite,
    onSecondary = OffWhite,
    onTertiary = OffWhite,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal
)

@Composable
fun ListaGamificadaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
