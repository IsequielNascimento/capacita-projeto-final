package com.example.capacita_projeto_final.core.notification

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.example.capacita_projeto_final.R

// MARK: - Text

sealed interface NotificationText {
    data class Resource(@param:StringRes val id: Int, val args: List<Any> = emptyList()) : NotificationText
    data class Plural(@param:PluralsRes val id: Int, val count: Int) : NotificationText
}

// MARK: - Notifications

enum class VisitNotificationAction(@param:StringRes val titleRes: Int) {
    SyncNow(R.string.sync_action),
    Retry(R.string.notification_action_retry),
}

sealed interface VisitNotification {
    data class Saved(
        val installationCode: String,
        val reading: Int,
        val pending: Int,
    ) : VisitNotification

    data object Syncing : VisitNotification

    data class SyncCompleted(val synchronized: Int) : VisitNotification

    data class SyncFailed(val pending: Int) : VisitNotification
}

// MARK: - Content

data class VisitNotificationContent(
    val title: NotificationText,
    val body: NotificationText,
    val action: VisitNotificationAction? = null,
    val ongoing: Boolean = false,
    val autoCancel: Boolean = true,
    val indeterminateProgress: Boolean = false,
    /**
     * Todas as notificações dividem o mesmo id, então cada atualização decide se
     * volta a avisar. O progresso do envio atualiza em silêncio; salvar uma visita
     * e o resultado do envio avisam de novo.
     */
    val alertsOnce: Boolean = false,
)

fun VisitNotification.toContent(): VisitNotificationContent = when (this) {
    is VisitNotification.Saved -> VisitNotificationContent(
        title = NotificationText.Resource(R.string.notification_saved_title),
        body = NotificationText.Resource(
            R.string.notification_saved_body,
            listOf(installationCode, reading),
        ),
        action = VisitNotificationAction.SyncNow.takeIf { pending > 0 },
    )

    VisitNotification.Syncing -> VisitNotificationContent(
        title = NotificationText.Resource(R.string.notification_syncing_title),
        body = NotificationText.Resource(R.string.notification_syncing_body),
        ongoing = true,
        autoCancel = false,
        indeterminateProgress = true,
        alertsOnce = true,
    )

    is VisitNotification.SyncCompleted -> VisitNotificationContent(
        title = NotificationText.Resource(R.string.notification_sync_completed_title),
        body = NotificationText.Plural(R.plurals.sync_visits_sent, synchronized),
    )

    is VisitNotification.SyncFailed -> VisitNotificationContent(
        title = NotificationText.Resource(R.string.notification_sync_failed_title),
        body = NotificationText.Plural(R.plurals.notification_visits_pending, pending),
        action = VisitNotificationAction.Retry.takeIf { pending > 0 },
    )
}

/**
 * Resultado de uma sincronização disparada pela notificação.
 * Retorna `null` quando não há nada a informar e a notificação deve sair da bandeja.
 */
fun syncResultNotification(synchronized: Int, remaining: Int): VisitNotification? = when {
    remaining > 0 -> VisitNotification.SyncFailed(remaining)
    synchronized > 0 -> VisitNotification.SyncCompleted(synchronized)
    else -> null
}
