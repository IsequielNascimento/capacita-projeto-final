package com.example.capacita_projeto_final.features.sync.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.example.capacita_projeto_final.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.capacita_projeto_final.ui.components.HigBorderedButton
import com.example.capacita_projeto_final.ui.components.HigListSection
import com.example.capacita_projeto_final.ui.components.HigRow
import com.example.capacita_projeto_final.ui.components.HigRowSeparator
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme

@Composable
fun SyncSection(state: SyncUiState, onSync: () -> Unit) {
    val colors = HigTheme.colors
    val progressDescription = state.progress?.let {
        stringResource(R.string.sync_progress_description, it.completed, it.total)
    } ?: stringResource(R.string.sync_in_progress)
    HigListSection(header = stringResource(R.string.sync_header)) {
        HigRow {
            Text(stringResource(R.string.sync_pending), style = HigTheme.typography.body, color = colors.label)
            Box(Modifier.weight(1f))
            Text(
                text = state.pending.toString(),
                style = HigTheme.typography.body,
                color = if (state.pending > 0) colors.accent else colors.secondaryLabel,
            )
        }
        HigRowSeparator()
        HigRow {
            Text(stringResource(R.string.sync_sent), style = HigTheme.typography.body, color = colors.label)
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
                Text(stringResource(R.string.sync_failed), style = HigTheme.typography.body, color = colors.label)
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
                title = stringResource(R.string.sync_action),
                onClick = onSync,
                inProgress = state.running,
                progressLabel = stringResource(R.string.sync_in_progress),
            )
            state.progress?.takeIf { it.total > 0 }?.let { progress ->
                LinearProgressIndicator(
                    progress = { progress.fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = HigMetrics.elementSpacing)
                        .clip(HigShapes.badge)
                        .semantics {
                            contentDescription = progressDescription
                        },
                    color = colors.accent,
                    trackColor = colors.fill,
                )
            }
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
