package com.example.listagamificada.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listagamificada.data.local.entity.TaskEntity

@Composable
fun TaskRow(
    task: TaskEntity,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    // Custom Colors
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    val difficultyColor = when (task.difficulty) {
        "Médio" -> Color.Yellow
        "Difícil" -> neonPink
        else -> Color.Cyan
    }

    val alpha by animateFloatAsState(targetValue = if (task.completed) 0.5f else 1f, label = "alpha")
    val textDecoration = if (task.completed) TextDecoration.LineThrough else null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(navyBlue, cyberPurple.copy(alpha = 0.2f))
                )
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = neonPink,
                    uncheckedColor = offWhite.copy(alpha = 0.7f),
                    checkmarkColor = navyBlue
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = offWhite,
                    textDecoration = textDecoration
                )
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description!!,
                        fontSize = 14.sp,
                        color = offWhite.copy(alpha = 0.8f),
                        textDecoration = textDecoration
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(difficultyColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(task.difficulty.first().toString(), fontWeight = FontWeight.Bold, color = difficultyColor, fontSize = 16.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = neonPink.copy(alpha = 0.7f))
            }
        }
    }
}
