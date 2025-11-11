// Define o pacote onde a tela de desafios está localizada.
package com.example.listagamificada.ui.screens.challenges

// Importa as bibliotecas necessárias do Jetpack Compose.
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.listagamificada.viewmodel.ViewModelFactory

// Anotação que marca a função como um componente de UI do Jetpack Compose.
@Composable
// Define a função da tela de desafios, que recebe um ViewModelFactory.
fun ChallengesScreen(factory: ViewModelFactory) {

    // Cria um container Box que ocupa tod o espaço da tela.
    Box(
        modifier = Modifier.fillMaxSize(), // Modificador para preencher o tamanho máximo.
        contentAlignment = Alignment.Center // Alinha o conteúdo no centro do Box.
    ) {
        // Exibe um texto informativo na tela.
        Text(text = "Tela de Desafios (Em Construção)")
    }
}
