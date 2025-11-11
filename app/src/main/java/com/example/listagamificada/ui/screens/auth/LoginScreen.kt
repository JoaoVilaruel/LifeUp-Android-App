// Define o pacote para as telas de autenticação.
package com.example.listagamificada.ui.screens.auth

// Importações de bibliotecas do Jetpack Compose e outras dependências.
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listagamificada.util.UiState
import com.example.listagamificada.viewmodel.AuthViewModel

// Composable para a tela de Login e Registro.
@Composable
fun LoginScreen(
    factory: ViewModelProvider.Factory, // Factory para criar o ViewModel.
    onLoginSuccess: () -> Unit // Callback para ser executado em caso de sucesso no login.
) {
    // Obtém a instância do AuthViewModel.
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    // Coleta o estado do login do ViewModel.
    val loginState by authViewModel.loginState.collectAsState()

    // Estados para armazenar os dados de entrada do usuário.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") } // Estado para o nome do usuário no registro.
    var isRegister by remember { mutableStateOf(false) } // Estado para alternar entre login e registro.

    // Definição das cores do tema Cyberpunk.
    val darkCharcoal = Color(0xFF1A1A2E)
    val navyBlue = Color(0xFF16213E)
    val neonPink = Color(0xFFE94560)
    val cyberPurple = Color(0xFF9f5fde)
    val offWhite = Color(0xFFF0F0F0)

    // Layout principal da tela com fundo em gradiente.
    Box(
        modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(colors = listOf(darkCharcoal, navyBlue)))
    ) {
        // Coluna para organizar os elementos verticalmente.
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo da aplicação.
            Icon(imageVector = Icons.Default.Star, contentDescription = "App Logo", modifier = Modifier.size(100.dp), tint = neonPink)
            Spacer(modifier = Modifier.height(8.dp))
            // Nome da aplicação.
            Text(text = "LifeUp", style = TextStyle(color = offWhite, fontSize = 40.sp, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(48.dp))

            // Cores personalizadas para os campos de texto.
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = neonPink, unfocusedBorderColor = offWhite.copy(alpha = 0.5f),
                focusedLabelColor = neonPink, unfocusedLabelColor = offWhite.copy(alpha = 0.7f),
                cursorColor = neonPink, focusedTextColor = offWhite, unfocusedTextColor = offWhite,
                focusedLeadingIconColor = neonPink, unfocusedLeadingIconColor = offWhite.copy(alpha = 0.7f)
            )

            // Campo de nome, visível apenas no modo de registro.
            AnimatedVisibility(visible = isRegister) {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome de Usuário") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Campo de Email.
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Campo de Senha.
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Botão de ação (Entrar ou Criar Conta).
            Button(
                onClick = {
                    if (isRegister) authViewModel.register(email, password, name)
                    else authViewModel.login(email, password)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                // Estilo do botão com gradiente.
                Box(
                    modifier = Modifier.fillMaxSize().background(brush = Brush.horizontalGradient(colors = listOf(cyberPurple, neonPink))).clip(RoundedCornerShape(100)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isRegister) "CRIAR CONTA" else "ENTRAR", color = offWhite, fontWeight = FontWeight.Bold)
                }
            }
            
            // Botão para alternar entre login e registro.
            TextButton(onClick = { isRegister = !isRegister }) {
                Text(text = if (isRegister) "Já tem conta? Faça login" else "Não tem conta? Cadastre-se", color = offWhite.copy(alpha = 0.8f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exibe o estado da operação (Carregando, Erro, Sucesso).
            when (val state = loginState) {
                is UiState.Loading -> CircularProgressIndicator(color = neonPink)
                is UiState.Error -> Text("Erro: ${state.message}", color = neonPink)
                is UiState.Success -> {
                    // Chama o callback de sucesso ao efetuar login.
                    LaunchedEffect(Unit) { onLoginSuccess() }
                }
                else -> {} // Estado inicial ou ocioso.
            }
        }
    }
}
