package com.example.capacita_projeto_final.core.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.capacita_projeto_final.MainActivity
import com.example.capacita_projeto_final.R

/**
 * Publica a notificação de visita salva e as atualizações de sincronização.
 *
 * Sempre a mesma notificação é reaproveitada, então salvar várias visitas
 * atualiza a bandeja em vez de empilhar avisos. Sem permissão, todo método
 * vira um no-op: o registro da visita nunca depende da notificação.
 */
class VisitNotifier(private val context: Context) {

    @SuppressLint("MissingPermission")
    fun show(notification: VisitNotification) {
        if (!canPostNotifications()) return

        val content = notification.toContent()
        val builder = NotificationCompat.Builder(context, NotificationChannels.Visits)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.resolve(content.title))
            .setContentText(context.resolve(content.body))
            .setContentIntent(openAppIntent())
            .setOngoing(content.ongoing)
            .setAutoCancel(content.autoCancel)
            .setOnlyAlertOnce(content.alertsOnce)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (content.indeterminateProgress) {
            builder.setProgress(0, 0, true)
        }
        content.action?.let { action ->
            builder.addAction(
                R.drawable.ic_notification,
                context.getString(action.titleRes),
                syncIntent(),
            )
        }

        NotificationManagerCompat.from(context).notify(NotificationId, builder.build())
    }

    fun dismiss() {
        NotificationManagerCompat.from(context).cancel(NotificationId)
    }

    private fun canPostNotifications(): Boolean {
        val granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        return granted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun openAppIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(context, RequestOpenApp, intent, PendingIntentFlags)
    }

    private fun syncIntent(): PendingIntent {
        val intent = Intent(context, SyncActionReceiver::class.java)
            .setAction(SyncActionReceiver.ActionSyncNow)
        return PendingIntent.getBroadcast(context, RequestSync, intent, PendingIntentFlags)
    }

    private companion object {
        const val NotificationId = 1001
        const val RequestOpenApp = 1
        const val RequestSync = 2
        const val PendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    }
}

private fun Context.resolve(text: NotificationText): String = when (text) {
    is NotificationText.Resource -> getString(text.id, *text.args.toTypedArray())
    is NotificationText.Plural -> resources.getQuantityString(text.id, text.count, text.count)
}
