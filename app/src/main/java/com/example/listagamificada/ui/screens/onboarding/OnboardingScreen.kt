package com.example.listagamificada.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val backgroundColor: Color,
    val primaryColor: Color,
    val buttonColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    val navyBlue = Color(0xFF16213E)
    val cyberPurple = Color(0xFF9f5fde)
    val neonPink = Color(0xFFE94560)
    val offWhite = Color(0xFFF0F0F0)

    val pages = listOf(
        OnboardingPage(
            title = "Bem-vindo(a) ao Lista Gamificada!",
            description = "É hora de se divertir enquanto faz suas tarefas! Transforme seus objetivos em uma aventura.",
            backgroundColor = navyBlue,
            primaryColor = offWhite,
            buttonColor = cyberPurple
        ),
        OnboardingPage(
            title = "Progresso no Jogo, Progresso na Vida",
            description = "Desbloqueie recompensas no jogo ao completar suas tarefas da vida real. Ganhe XP e moedas para evoluir.",
            backgroundColor = cyberPurple,
            primaryColor = offWhite,
            buttonColor = neonPink
        ),
        OnboardingPage(
            title = "Combata Monstros e Socialize",
            description = "Mantenha seus objetivos sob controle com a ajuda de seus amigos. Ajudem-se na vida e na batalha!",
            backgroundColor = neonPink,
            primaryColor = offWhite,
            buttonColor = navyBlue
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val currentPage = pages[pagerState.currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentPage.backgroundColor)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        Column(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { index ->
                    val color = if (pagerState.currentPage == index) currentPage.primaryColor else Color.Gray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onOnboardingComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = currentPage.buttonColor)
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "Próximo" else "Vamos Começar!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for future images/icons
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            color = page.primaryColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = page.primaryColor.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}