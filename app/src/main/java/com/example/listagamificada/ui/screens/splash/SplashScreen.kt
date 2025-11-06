package com.example.listagamificada.ui.screens.splash

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

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "Splash Alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // The total time the splash screen is visible
        onTimeout()
    }

    // Custom Colors from our theme
    val darkCharcoal = Color(0xFF1A1A2E)
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val offWhite = Color(0xFFF0F0F0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(darkCharcoal, navyBlue))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim.value)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp),
                tint = neonPink
            )
            Spacer(modifier = Modifier.height(8.dp))
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
