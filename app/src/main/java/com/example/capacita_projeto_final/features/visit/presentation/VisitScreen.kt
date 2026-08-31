package com.example.capacita_projeto_final.features.visit.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.Muted
import com.example.capacita_projeto_final.ui.theme.Success

@Composable
fun VisitScreen(
    state: VisitUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onFinish: () -> Unit,
) {
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(onClick = onBack) { Text("‹ Ponto") }
            Text("Registro de visita", style = MaterialTheme.typography.labelLarge, color = Muted)
            when (state) {
                VisitUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                is VisitUiState.Error -> ErrorContent(state.message, onBack)
                is VisitUiState.Ready -> ReadyContent(state, onSave)
                is VisitUiState.Saved -> SavedContent(state, onFinish)
            }
        }
    }
}

@Composable
private fun ReadyContent(state: VisitUiState.Ready, onSave: () -> Unit) {
    Text(state.point.customer, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resumo", fontWeight = FontWeight.Bold)
            Text("Instalação: ${state.point.installationCode}", color = Muted)
            Text("Medidor: ${state.point.meterNumber}", color = Muted)
            Text("Leitura anterior: ${state.point.previousReading}", color = Muted)
            Text("Leitura atual: ${state.reading}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
    Text(
        "A visita será gravada no Room e ficará disponível mesmo sem conexão.",
        color = Muted,
    )
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.saving,
        onClick = onSave,
    ) {
        if (state.saving) {
            CircularProgressIndicator(
                modifier = Modifier.height(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Text("Salvar visita no dispositivo")
        }
    }
}

@Composable
private fun SavedContent(state: VisitUiState.Saved, onFinish: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Visita salva", style = MaterialTheme.typography.headlineSmall, color = Success, fontWeight = FontWeight.Bold)
            Text("${state.point.customer} · leitura ${state.reading}")
            Text("Status: aguardando sincronização", color = Muted)
        }
    }
    Button(modifier = Modifier.fillMaxWidth(), onClick = onFinish) { Text("Voltar para a rota") }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Spacer(Modifier.height(24.dp))
    Text("Não foi possível concluir", style = MaterialTheme.typography.headlineSmall)
    Text(message, color = MaterialTheme.colorScheme.error)
    OutlinedButton(onClick = onBack) { Text("Voltar") }
}
