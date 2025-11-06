package com.example.listagamificada.ui.screens.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listagamificada.data.local.entity.StatsEntity

@Composable
fun RankingScreen(factory: androidx.lifecycle.ViewModelProvider.Factory) {
    // Custom Colors
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    // Dummy data for ranking
    val dummyRanking = listOf(
        StatsEntity(userId = "ProPlayer123", points = 1250, badges = ""),
        StatsEntity(userId = "TaskMaster", points = 980, badges = ""),
        StatsEntity(userId = "SpeedRunner", points = 720, badges = ""),
        StatsEntity(userId = "Você", points = 450, badges = ""),
        StatsEntity(userId = "Newbie", points = 120, badges = ""),
        StatsEntity(userId = "JustForFun", points = 50, badges = "")
    ).sortedByDescending { it.points }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(dummyRanking) { index, playerStats ->
            RankingItem(rank = index + 1, stats = playerStats, isCurrentUser = playerStats.userId == "Você")
        }
    }
}

@Composable
fun RankingItem(rank: Int, stats: StatsEntity, isCurrentUser: Boolean) {
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> null
    }

    val backgroundBrush = Brush.horizontalGradient(
        colors = listOf(navyBlue, cyberPurple.copy(alpha = 0.4f))
    )

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
            Text(
                text = "#$rank",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = medalColor ?: offWhite
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = offWhite,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(navyBlue.copy(alpha = 0.5f))
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stats.userId,
                fontWeight = FontWeight.SemiBold,
                color = offWhite,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            medalColor?.let {
                Icon(Icons.Default.Person, contentDescription = "Medal", tint = it)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "${stats.points} pts",
                fontWeight = FontWeight.Bold,
                color = neonPink
            )
        }
    }
}
