package com.example.capacita_projeto_final.features.sync.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.Muted

@Composable
fun SyncPanel(state: SyncUiState, onSync: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Sincronização", fontWeight = FontWeight.Bold)
                    Text("Retrofit · GET + POST", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
                Text("${state.pending} pendente(s)", color = MaterialTheme.colorScheme.primary)
            }
            Text("${state.synced} enviada(s) · ${state.errors} com erro", color = Muted)
            state.message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.running,
                onClick = onSync,
            ) {
                if (state.running) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(2.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Verificar serviço e sincronizar")
                }
            }
        }
    }
}
