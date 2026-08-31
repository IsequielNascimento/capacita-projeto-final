package com.example.capacita_projeto_final.features.sync.presentation

fun SyncFeedback.readableMessage(): String = when (this) {
    is SyncFeedback.Completed ->
        if (synchronized == 1) "1 visita enviada." else "$synchronized visitas enviadas."

    is SyncFeedback.PartiallyFailed ->
        if (failed == 1) {
            "$synchronized enviadas e 1 sem envio."
        } else {
            "$synchronized enviadas e $failed sem envio."
        }

    SyncFeedback.NothingPending -> "Nenhuma visita aguardando envio."
    SyncFeedback.ServiceUnavailable -> "Não foi possível conectar ao servidor."
}
