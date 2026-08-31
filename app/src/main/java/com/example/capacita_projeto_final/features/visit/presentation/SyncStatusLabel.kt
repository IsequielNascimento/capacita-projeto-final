package com.example.capacita_projeto_final.features.visit.presentation

import com.example.capacita_projeto_final.features.visit.domain.SyncStatus

fun SyncStatus.readableLabel(): String = when (this) {
    SyncStatus.Pending -> "Aguardando envio"
    SyncStatus.Sending -> "Enviando"
    SyncStatus.Sent -> "Enviada"
    SyncStatus.Failed -> "Falha no envio"
}
