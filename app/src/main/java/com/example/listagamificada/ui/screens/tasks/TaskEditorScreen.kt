package com.example.listagamificada.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

data class CategoryOption(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(factory: ViewModelProvider.Factory, taskId: String? = null, onSaved: () -> Unit) {
    val taskViewModel: TaskViewModel = viewModel(factory = factory)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Fácil") }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val uid = taskViewModel.getUserId() ?: ""

    val darkCharcoal = Color(0xFF1A1A2E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    val categories = listOf(
        CategoryOption("Pessoal", Icons.Default.Person),
        CategoryOption("Trabalho", Icons.Default.Build),
        CategoryOption("Estudos", Icons.Default.MailOutline),
        CategoryOption("Saúde", Icons.Default.Favorite),
        CategoryOption("Casa", Icons.Default.Home)
    )
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    // CORREÇÃO: Adicionando o Scaffold e a TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Nova Tarefa" else "Editar Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onSaved) { // onSaved fecha a tela
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkCharcoal)
                .padding(paddingValues) // Usa o padding do Scaffold
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenu(
                    label = "Categoria",
                    options = categories,
                    selectedOption = selectedCategory,
                    onOptionSelected = { selectedCategory = it },
                    modifier = Modifier.weight(1f),
                    optionToString = { it.label }
                )
                ExposedDropdownMenu(
                    label = "Dificuldade",
                    options = listOf("Fácil", "Médio", "Difícil"),
                    selectedOption = difficulty,
                    onOptionSelected = { difficulty = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dueDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Data de Vencimento") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data", tint = offWhite)
                    }
                }
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { 
                            dueDate = datePickerState.selectedDateMillis
                            showDatePicker = false 
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = true))

            // CORREÇÃO: Removido o botão de Cancelar para evitar redundância
            Button(
                onClick = {
                    if (title.isNotBlank() && uid.isNotEmpty()) {
                        val task = TaskEntity(
                            id = taskId ?: UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            ownerId = uid,
                            difficulty = difficulty,
                            category = selectedCategory.label,
                            dueDate = dueDate
                        )
                        taskViewModel.addTask(task)
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp), // Botão ocupa a largura toda
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = Brush.horizontalGradient(colors = listOf(cyberPurple, neonPink)))
                        .clip(RoundedCornerShape(100)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (taskId == null) "Criar Tarefa" else "Salvar Alterações", color = offWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ExposedDropdownMenu(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionToString: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = optionToString(selectedOption),
            onValueChange = {}, // read-only
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE94560),
                unfocusedBorderColor = Color(0xFFF0F0F0).copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFFE94560),
                unfocusedLabelColor = Color(0xFFF0F0F0).copy(alpha = 0.7f),
                focusedTextColor = Color(0xFFF0F0F0),
                unfocusedTextColor = Color(0xFFF0F0F0)
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionToString(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
