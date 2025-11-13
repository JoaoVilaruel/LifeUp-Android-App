package com.example.listagamificada.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.ui.navigation.Screen
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class TaskFilter { ALL, ACTIVE, COMPLETED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    mainViewModel: MainViewModel, 
    authViewModel: AuthViewModel, 
    onOpenEditor: (String?) -> Unit,
    navController: NavController
) {
    val tasksState by mainViewModel.tasks.collectAsState()
    val userId = authViewModel.getUserId()
    var currentFilter by remember { mutableStateOf(TaskFilter.ALL) }

    LaunchedEffect(userId) {
        userId?.let { mainViewModel.loadTasksForUser(it) }
    }

    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Screen.Tasks.label) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenEditor(null) },
                containerColor = Color.Transparent,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(brush = Brush.horizontalGradient(colors = listOf(cyberPurple, neonPink))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = tasksState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    val tasks = state.data
                    val filteredTasks = when (currentFilter) {
                        TaskFilter.ALL -> tasks
                        TaskFilter.ACTIVE -> tasks.filter { !it.completed }
                        TaskFilter.COMPLETED -> tasks.filter { it.completed }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TaskFilterTabs(currentFilter) { currentFilter = it }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (filteredTasks.isEmpty()) {
                        EmptyState()
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(filteredTasks, key = { it.id }) { task ->
                                TaskItem(task = task, onToggle = { 
                                    if (userId != null) {
                                        mainViewModel.toggleTaskCompletion(task, userId)
                                    }
                                }, onEdit = { onOpenEditor(task.id) }, onDelete = { mainViewModel.deleteTask(task) })
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    EmptyState(message = "Ocorreu um erro ao carregar as tarefas.")
                }
                else -> {}
            }
        }
    }
}

// Itens da BottomBar
val bottomNavItems = listOf(
    Screen.Tasks,
    Screen.Quotes,
    Screen.Challenges,
    Screen.Ranking,
    // CORREÇÃO: Removendo a tela de Loja
    Screen.Profile
)

@Composable
fun TaskFilterTabs(currentFilter: TaskFilter, onFilterChange: (TaskFilter) -> Unit) {
    val filters = listOf("Todas" to TaskFilter.ALL, "Ativas" to TaskFilter.ACTIVE, "Feitas" to TaskFilter.COMPLETED)
    TabRow(
        selectedTabIndex = filters.indexOfFirst { it.second == currentFilter },
        containerColor = Color.Transparent,
        contentColor = Color(0xFFE94560),
        divider = {}
    ) {
        filters.forEach { (title, filter) ->
            Tab(
                selected = currentFilter == filter,
                onClick = { onFilterChange(filter) },
                text = { Text(title, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@Composable
fun TaskItem(task: TaskEntity, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val navyBlue = Color(0xFF16213E)
    val offWhite = Color(0xFFF0F0F0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(navyBlue.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CompletionCircle(isCompleted = task.completed, onToggle = onToggle)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, color = offWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                task.description?.let { Text(it, color = offWhite.copy(alpha = 0.7f), fontSize = 14.sp) }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag(task.category)
                    Tag("+${task.difficultyToXp()} XP")
                    task.dueDate?.let { Tag(SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(it))) }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = offWhite.copy(alpha = 0.7f)) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color(0xFFE94560)) }
            }
        }
    }
}

@Composable
fun CompletionCircle(isCompleted: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            .background(if (isCompleted) Color(0xFF4CAF50) else Color.Transparent)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun Tag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EmptyState(message: String = "Nenhuma tarefa. Toque em + para adicionar.") {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

fun TaskEntity.difficultyToXp(): Int {
    return when (difficulty) {
        "Fácil" -> 10
        "Médio" -> 25
        "Difícil" -> 50
        else -> 0
    }
}
