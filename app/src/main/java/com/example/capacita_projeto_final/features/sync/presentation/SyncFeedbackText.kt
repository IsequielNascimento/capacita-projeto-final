package com.example.capacita_projeto_final.features.sync.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.capacita_projeto_final.R

@Composable
fun SyncFeedback.readableMessage(): String = when (this) {
    is SyncFeedback.Completed ->
        pluralStringResource(R.plurals.sync_visits_sent, synchronized, synchronized) + "."

    is SyncFeedback.PartiallyFailed -> stringResource(
        R.string.sync_partial_format,
        pluralStringResource(R.plurals.sync_visits_sent, synchronized, synchronized),
        pluralStringResource(R.plurals.sync_visits_failed, failed, failed),
    )

    SyncFeedback.NothingPending -> stringResource(R.string.sync_nothing_pending)
    SyncFeedback.ServiceUnavailable -> stringResource(R.string.sync_service_unavailable)
}
