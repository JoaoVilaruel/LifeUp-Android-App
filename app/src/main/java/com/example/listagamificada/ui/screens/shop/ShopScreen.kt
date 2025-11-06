package com.example.listagamificada.ui.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.viewmodel.ProfileViewModel

data class ShopItem(val id: String, val name: String, val price: Int, val icon: ImageVector)

@Composable
fun ShopScreen(factory: ViewModelProvider.Factory) {
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val items = listOf(
        ShopItem("1", "Tema Cyber", 50, Icons.Default.Build),
        ShopItem("2", "Avatar Fantasma", 100, Icons.Default.AccountCircle),
        ShopItem("3", "Badge Lendário", 200, Icons.Default.Star)
    )

    val uid = profileViewModel.getUserId() ?: ""
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            profileViewModel.loadStats(uid)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            ShopItemCard(item = item, onPurchase = {
                profileViewModel.addPoints(uid, -item.price)
            })
        }
    }
}

@Composable
fun ShopItemCard(item: ShopItem, onPurchase: () -> Unit) {
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(navyBlue.copy(alpha = 0.8f))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                modifier = Modifier.size(60.dp),
                tint = neonPink.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = offWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.price} pts",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = neonPink
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onPurchase,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
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
                    Text("COMPRAR", color = offWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
