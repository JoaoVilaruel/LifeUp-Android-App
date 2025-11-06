package com.example.listagamificada.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.viewmodel.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(factory: ViewModelProvider.Factory, taskId: String? = null, onSaved: () -> Unit) {
    val taskViewModel: TaskViewModel = viewModel(factory = factory)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Fácil") }

    val uid = taskViewModel.getUserId() ?: ""

    // Custom Colors
    val darkCharcoal = Color(0xFF1A1A2E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    // TODO: load existing task if taskId provided

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkCharcoal)
            .padding(16.dp)
    ) {
        Text(
            text = if (taskId == null) "Nova Missão" else "Editar Missão",
            style = MaterialTheme.typography.headlineMedium,
            color = offWhite,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = neonPink,
            unfocusedBorderColor = offWhite.copy(alpha = 0.5f),
            focusedLabelColor = neonPink,
            unfocusedLabelColor = offWhite.copy(alpha = 0.7f),
            cursorColor = neonPink,
            focusedTextColor = offWhite,
            unfocusedTextColor = offWhite
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título da Missão") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Detalhes da Missão (Opcional)") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Dificuldade", style = MaterialTheme.typography.titleMedium, color = offWhite)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Fácil", "Médio", "Difícil").forEach { diff ->
                FilterChip(
                    selected = difficulty == diff,
                    onClick = { difficulty = diff },
                    label = { Text(diff) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = neonPink,
                        selectedLabelColor = darkCharcoal,
                        labelColor = offWhite,
                        iconColor = offWhite
                    ),
                    border = if (difficulty == diff) null else BorderStroke(1.dp, offWhite.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (title.isNotBlank() && uid.isNotEmpty()) {
                    val task = TaskEntity(
                        id = taskId ?: UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        ownerId = uid,
                        difficulty = difficulty
                    )
                    taskViewModel.addTask(task)
                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
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
                Text("SALVAR MISSÃO", color = offWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}
