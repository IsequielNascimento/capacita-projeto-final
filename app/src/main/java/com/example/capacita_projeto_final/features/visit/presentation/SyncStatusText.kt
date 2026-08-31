package com.example.capacita_projeto_final.features.visit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.capacita_projeto_final.R
import com.example.capacita_projeto_final.features.visit.domain.SyncStatus

@Composable
fun SyncStatus.readableLabel(): String = stringResource(
    when (this) {
        SyncStatus.Pending -> R.string.sync_status_pending
        SyncStatus.Sending -> R.string.sync_status_sending
        SyncStatus.Sent -> R.string.sync_status_sent
        SyncStatus.Failed -> R.string.sync_status_failed
    },
)
