package com.example.capacita_projeto_final.features.sync.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.capacita_projeto_final.ui.components.HigBorderedButton
import com.example.capacita_projeto_final.ui.components.HigListSection
import com.example.capacita_projeto_final.ui.components.HigRow
import com.example.capacita_projeto_final.ui.components.HigRowSeparator
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigTheme

@Composable
fun SyncSection(state: SyncUiState, onSync: () -> Unit) {
    val colors = HigTheme.colors
    HigListSection(header = "Envio das visitas") {
        HigRow {
            Text("Aguardando envio", style = HigTheme.typography.body, color = colors.label)
            Box(Modifier.weight(1f))
            Text(
                text = state.pending.toString(),
                style = HigTheme.typography.body,
                color = if (state.pending > 0) colors.accent else colors.secondaryLabel,
            )
        }
        HigRowSeparator()
        HigRow {
            Text("Enviadas", style = HigTheme.typography.body, color = colors.label)
            Box(Modifier.weight(1f))
            Text(
                text = state.synced.toString(),
                style = HigTheme.typography.body,
                color = colors.secondaryLabel,
            )
        }
        if (state.errors > 0) {
            HigRowSeparator()
            HigRow {
                Text("Com falha no envio", style = HigTheme.typography.body, color = colors.label)
                Box(Modifier.weight(1f))
                Text(
                    text = state.errors.toString(),
                    style = HigTheme.typography.bodyEmphasized,
                    color = colors.destructive,
                )
            }
        }
        HigRowSeparator()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(HigMetrics.contentMargin),
        ) {
            HigBorderedButton(
                title = "Enviar agora",
                onClick = onSync,
                inProgress = state.running,
                progressLabel = "Enviando",
            )
            state.feedback?.let { feedback ->
                Text(
                    modifier = Modifier.padding(top = HigMetrics.elementSpacing),
                    text = feedback.readableMessage(),
                    style = HigTheme.typography.footnote,
                    color = if (state.failed) colors.destructive else colors.secondaryLabel,
                )
            }
        }
    }
}
