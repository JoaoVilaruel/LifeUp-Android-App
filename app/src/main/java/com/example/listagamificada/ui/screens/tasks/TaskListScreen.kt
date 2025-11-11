// Define o pacote para as telas de tarefas.
package com.example.listagamificada.ui.screens.tasks

// Importações de bibliotecas do Jetpack Compose e outras dependências.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.listagamificada.ui.components.TaskRow
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

// Opt-in para usar APIs experimentais do Material 3.
@OptIn(ExperimentalMaterial3Api::class)
// Composable para a tela que exibe a lista de tarefas.
@Composable
fun TaskListScreen(
    mainViewModel: MainViewModel, // ViewModel principal para gerenciar os dados das tarefas.
    authViewModel: AuthViewModel, // ViewModel de autenticação para obter informações do usuário.
    onOpenEditor: (String?) -> Unit = {} // Callback para abrir o editor de tarefas.
) {
    // Coleta o estado da lista de tarefas do ViewModel.
    val tasksState by mainViewModel.tasks.collectAsState()
    // Obtém o ID do usuário logado.
    val userId = authViewModel.getUserId()
    // Estado para o Snackbar (mensagens temporárias na parte inferior da tela).
    val snackbarHostState = remember { SnackbarHostState() }

    // Efeito para observar eventos da UI (como mensagens de erro ou sucesso) e exibir no Snackbar.
    LaunchedEffect(Unit) {
        mainViewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Efeito para carregar as tarefas do usuário quando a tela é iniciada.
    LaunchedEffect(userId) {
        if (userId != null) {
            mainViewModel.loadTasksForUser(userId)
        }
    }

    // Scaffold fornece a estrutura básica da tela do Material Design.
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) } // Conecta o Snackbar ao Scaffold.
    ) { innerPadding ->
        // Cores do tema.
        val neonPink = Color(0xFFE94560)
        val offWhite = Color(0xFFF0F0F0)

        // Gerencia a exibição da UI com base no estado do carregamento dos dados.
        when (val state = tasksState) {
            is UiState.Loading, is UiState.Idle -> {
                // Exibe um indicador de progresso enquanto os dados estão sendo carregados.
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = neonPink)
                }
            }
            is UiState.Error -> {
                // Exibe uma mensagem de erro se o carregamento falhar.
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro: ${state.message}", color = neonPink)
                }
            }
            is UiState.Success -> {
                // Se o carregamento for bem-sucedido, exibe a lista de tarefas.
                val list = state.data
                if (list.isEmpty()) {
                    // Exibe uma mensagem se a lista de tarefas estiver vazia.
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nenhuma tarefa. Toque em + para adicionar.", color = offWhite.copy(alpha = 0.8f))
                    }
                } else {
                    // Exibe a lista de tarefas em uma coluna rolável.
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        items(list, key = { it.id }) { task ->
                            // Composable para cada linha da tarefa.
                            TaskRow(task = task, onToggleComplete = {
                                if (userId != null) {
                                    mainViewModel.toggleTaskCompletion(task, userId)
                                }
                            }, onClick = { onOpenEditor(task.id) }, onDelete = {
                                // TODO: Implementar a exclusão de tarefas no MainViewModel.
                            })
                        }
                    }
                }
            }
        }
    }
}
