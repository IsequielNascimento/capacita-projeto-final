package com.example.capacita_projeto_final.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.capacita_projeto_final.CapacitaApplication
import kotlinx.coroutines.launch

/**
 * Executa a sincronização a partir da ação da notificação, sem abrir o app.
 *
 * O trabalho roda no escopo da Application porque o receiver morre assim que
 * `onReceive` retorna; `goAsync` mantém o processo vivo até o envio terminar.
 */
class SyncActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ActionSyncNow) return
        val container = (context.applicationContext as? CapacitaApplication)?.container ?: return

        val pendingResult = goAsync()
        container.applicationScope.launch {
            try {
                container.visitNotifier.show(VisitNotification.Syncing)

                val outcome = runCatching { container.syncRepository.synchronizePendingVisits() }
                val remaining = runCatching { container.visitRepository.pendingVisits().size }.getOrDefault(0)
                val result = syncResultNotification(
                    synchronized = outcome.getOrNull()?.synchronized ?: 0,
                    remaining = remaining,
                )

                if (result == null) {
                    container.visitNotifier.dismiss()
                } else {
                    container.visitNotifier.show(result)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ActionSyncNow = "com.example.capacita_projeto_final.action.SYNC_NOW"
    }
}
