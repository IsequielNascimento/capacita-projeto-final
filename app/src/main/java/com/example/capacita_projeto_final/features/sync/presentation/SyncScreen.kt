package com.example.capacita_projeto_final.features.sync.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.R
import com.example.capacita_projeto_final.features.visit.domain.SyncStatus
import com.example.capacita_projeto_final.features.visit.domain.Visit
import com.example.capacita_projeto_final.features.visit.presentation.readableLabel
import com.example.capacita_projeto_final.ui.components.HigEmptyState
import com.example.capacita_projeto_final.ui.components.HigHapticFeedback
import com.example.capacita_projeto_final.ui.components.HigHapticOutcome
import com.example.capacita_projeto_final.ui.components.HigLargeTitle
import com.example.capacita_projeto_final.ui.components.HigListSection
import com.example.capacita_projeto_final.ui.components.HigNavigationBar
import com.example.capacita_projeto_final.ui.components.HigRow
import com.example.capacita_projeto_final.ui.components.HigRowSeparator
import com.example.capacita_projeto_final.ui.components.rememberLargeTitleCollapsed
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigTheme
import com.example.capacita_projeto_final.ui.theme.rememberReadableContentPadding
import java.text.DateFormat
import java.util.Date

@Composable
fun SyncScreen(state: SyncUiState, onSync: () -> Unit) {
    val colors = HigTheme.colors
    val listState = rememberLazyListState()
    val collapsed = rememberLargeTitleCollapsed(listState)

    HigHapticFeedback(
        outcome = state.feedback?.let { feedback ->
            if (state.failed) HigHapticOutcome.Failure else HigHapticOutcome.Success
        },
        key = state.feedback,
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.groupedBackground),
    ) {
        HigNavigationBar(
            title = stringResource(R.string.sync_title),
            showsInlineTitle = collapsed,
            showsSeparator = collapsed,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = rememberReadableContentPadding(),
            verticalArrangement = Arrangement.spacedBy(HigMetrics.groupSpacing),
        ) {
            item {
                HigLargeTitle(
                    text = stringResource(R.string.sync_title),
                    subtitle = stringResource(R.string.sync_subtitle),
                )
            }
            item { SyncSection(state = state, onSync = onSync) }
            if (state.visits.isEmpty()) {
                item {
                    HigEmptyState(
                        title = stringResource(R.string.sync_empty_title),
                        message = stringResource(R.string.sync_empty_message),
                    )
                }
            } else {
                item {
                    HigListSection(header = stringResource(R.string.sync_history_header)) {
                        state.visits.forEachIndexed { index, visit ->
                            if (index > 0) HigRowSeparator()
                            VisitStatusRow(visit)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(HigMetrics.elementSpacing)) }
        }
    }
}

@Composable
private fun VisitStatusRow(visit: Visit) {
    val colors = HigTheme.colors
    HigRow {
        Column(Modifier.weight(1f)) {
            Text(visit.installationCode, style = HigTheme.typography.body, color = colors.label)
            Text(
                text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(Date(visit.capturedAt)),
                style = HigTheme.typography.footnote,
                color = colors.secondaryLabel,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = visit.syncStatus.readableLabel(),
            style = HigTheme.typography.subheadline,
            color = when (visit.syncStatus) {
                SyncStatus.Sent -> colors.success
                SyncStatus.Failed -> colors.destructive
                else -> colors.secondaryLabel
            },
        )
    }
}
