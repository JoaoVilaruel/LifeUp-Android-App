package com.example.listagamificada.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.ui.components.TaskRow
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    factory: ViewModelProvider.Factory,
    onOpenEditor: (String?) -> Unit = {}
) {
    val taskViewModel: TaskViewModel = viewModel(factory = factory)
    val tasksState by taskViewModel.tasks.collectAsState()
    val scope = rememberCoroutineScope()

    val uid = taskViewModel.getUserId() ?: ""
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            taskViewModel.loadTasks(uid)
        }
    }

    val neonPink = Color(0xFFE94560)
    val offWhite = Color(0xFFF0F0F0)

    when (val state = tasksState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = neonPink)
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Erro: ${state.message}", color = neonPink)
            }
        }
        is UiState.Success -> {
            val list = state.data
            if (list.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma tarefa. Toque em + para adicionar.", color = offWhite.copy(alpha = 0.8f))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(list, key = { it.id }) { task ->
                        TaskRow(task = task, onToggleComplete = {
                            val updated = task.copy(completed = !task.completed)
                            taskViewModel.updateTask(updated)
                        }, onClick = { onOpenEditor(task.id) }, onDelete = {
                            scope.launch {
                                taskViewModel.deleteTask(task)
                            }
                        })
                    }
                }
            }
        }
        is UiState.Idle -> {}
    }
}
