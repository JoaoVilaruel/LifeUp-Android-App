package com.example.listagamificada.ui.screens.quotes

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.listagamificada.data.remote.retrofit.QuoteResponse
import com.example.listagamificada.ui.navigation.Screen
import com.example.listagamificada.ui.screens.tasks.bottomNavItems
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.QuoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteScreen(quoteViewModel: QuoteViewModel, navController: NavController) { 
    val quoteState by quoteViewModel.quoteState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Frases") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                // CORREÇÃO: Usando a cor de fundo do tema atual.
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = quoteState) {
                is UiState.Loading, is UiState.Idle -> CircularProgressIndicator(color = Color(0xFFE94560))
                is UiState.Error -> Text("Falha ao buscar frase: ${state.message}", color = Color(0xFFE94560))
                is UiState.Success -> {
                    QuoteContent(quote = state.data, onRefresh = { quoteViewModel.fetchRandomQuote() })
                }
            }
        }
    }
}

@Composable
private fun QuoteContent(quote: QuoteResponse, onRefresh: () -> Unit) {
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(brush = Brush.verticalGradient(listOf(navyBlue, navyBlue.copy(alpha = 0.5f))))
                .padding(32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\"${quote.q}\"",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = offWhite,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "- ${quote.a}",
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    color = offWhite.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FloatingActionButton(
                onClick = { 
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "\"${quote.q}\" - ${quote.a}")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                containerColor = cyberPurple
            ) {
                Icon(Icons.Default.Share, contentDescription = "Compartilhar", tint = offWhite)
            }
            FloatingActionButton(
                onClick = onRefresh,
                containerColor = neonPink,
                modifier = Modifier.size(64.dp) // Main button
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Nova Inspiração", tint = offWhite)
            }
            FloatingActionButton(
                onClick = { 
                    val clipboardManager = context.getSystemService(android.content.ClipboardManager::class.java)
                    val clip = android.content.ClipData.newPlainText("quote", "\"${quote.q}\" - ${quote.a}")
                    clipboardManager.setPrimaryClip(clip)
                },
                containerColor = cyberPurple
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Copiar", tint = offWhite)
            }
        }
    }
}