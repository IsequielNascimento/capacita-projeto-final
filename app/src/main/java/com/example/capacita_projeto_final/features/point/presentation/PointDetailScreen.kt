package com.example.capacita_projeto_final.features.point.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.ui.components.NavigationTopBar
import com.example.capacita_projeto_final.ui.theme.Muted
import com.example.capacita_projeto_final.ui.theme.Success
import java.text.DateFormat
import java.util.Date

@Composable
fun PointDetailScreen(
    state: PointDetailUiState,
    onBack: () -> Unit,
    onReadingChange: (String) -> Unit,
    onStartVisit: () -> Unit,
) {
    Scaffold(
        topBar = {
            NavigationTopBar(
                title = "Detalhes do ponto",
                subtitle = "Rota Aldeota",
                onBack = onBack,
            )
        },
    ) { contentPadding ->
        when (state) {
            PointDetailUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }

            PointDetailUiState.NotFound -> Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Ponto não encontrado", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onBack) { Text("Voltar para a rota") }
            }

            is PointDetailUiState.Ready -> PointDetailContent(
                modifier = Modifier.padding(contentPadding),
                state = state,
                onReadingChange = onReadingChange,
                onStartVisit = onStartVisit,
            )
        }
    }
}

@Composable
private fun PointDetailContent(
    modifier: Modifier,
    state: PointDetailUiState.Ready,
    onReadingChange: (String) -> Unit,
    onStartVisit: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Ponto ${state.point.order}", style = MaterialTheme.typography.labelLarge, color = Muted)
        Text(state.point.customer, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        PointDataCard(state.point)
        state.latestVisit?.let { visit ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(18.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Última visita", fontWeight = FontWeight.Bold, color = Success)
                    Text("Leitura ${visit.currentReading}")
                    Text(DateFormat.getDateTimeInstance().format(Date(visit.capturedAt)), color = Muted)
                    Text("Status: ${visit.syncStatus}", color = Muted)
                }
            }
        }
        Text("Nova leitura", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.readingInput,
            onValueChange = onReadingChange,
            label = { Text("Leitura atual") },
            supportingText = { Text(state.validationMessage ?: "Anterior: ${state.point.previousReading}") },
            isError = state.validationMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
        Button(modifier = Modifier.fillMaxWidth(), onClick = onStartVisit) {
            Text("Continuar registro")
        }
    }
}

@Composable
private fun PointDataCard(point: RoutePoint) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PointDataRow("Instalação", point.installationCode)
            PointDataRow("Medidor", point.meterNumber)
            PointDataRow("Leitura anterior", point.previousReading.toString())
            PointDataRow("Referência", point.referencePoint)
            PointDataRow("Endereço", point.address)
        }
    }
}

@Composable
private fun PointDataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted)
        Text(modifier = Modifier.padding(start = 20.dp), text = value, fontWeight = FontWeight.SemiBold)
    }
}
