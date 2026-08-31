package com.example.capacita_projeto_final.core.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VisitNotificationTest {

    @Test
    fun savedVisitCarriesInstallationAndReadingAndOffersSync() {
        val content = VisitNotification.Saved(
            installationCode = "CAP-ALD-0001",
            reading = 125,
            pending = 3,
        ).toContent()

        val body = content.body as NotificationText.Resource
        assertEquals(listOf<Any>("CAP-ALD-0001", 125), body.args)
        assertEquals(VisitNotificationAction.SyncNow, content.action)
    }

    @Test
    fun savedVisitWithoutPendingWorkOffersNoAction() {
        val content = VisitNotification.Saved("CAP-ALD-0001", 125, pending = 0).toContent()

        assertNull(content.action)
    }

    @Test
    fun syncingStaysInTrayWithIndeterminateProgress() {
        val content = VisitNotification.Syncing.toContent()

        assertTrue(content.ongoing)
        assertTrue(content.indeterminateProgress)
        assertFalse(content.autoCancel)
        assertNull(content.action)
    }

    @Test
    fun everySavedVisitAlertsAgainEvenReusingTheSameNotification() {
        val content = VisitNotification.Saved("CAP-ALD-0001", 125, pending = 1).toContent()

        assertFalse(content.alertsOnce)
    }

    @Test
    fun syncProgressUpdatesTheTraySilently() {
        assertTrue(VisitNotification.Syncing.toContent().alertsOnce)
    }

    @Test
    fun syncResultAlertsAgain() {
        assertFalse(VisitNotification.SyncCompleted(3).toContent().alertsOnce)
        assertFalse(VisitNotification.SyncFailed(2).toContent().alertsOnce)
    }

    @Test
    fun completedSyncReportsTheCountAndOffersNoAction() {
        val content = VisitNotification.SyncCompleted(synchronized = 3).toContent()

        assertEquals(3, (content.body as NotificationText.Plural).count)
        assertNull(content.action)
        assertTrue(content.autoCancel)
    }

    @Test
    fun failedSyncOffersRetryWhileVisitsRemain() {
        val content = VisitNotification.SyncFailed(pending = 2).toContent()

        assertEquals(VisitNotificationAction.Retry, content.action)
        assertEquals(2, (content.body as NotificationText.Plural).count)
    }

    @Test
    fun remainingVisitsWinOverPartialSuccess() {
        val result = syncResultNotification(synchronized = 1, remaining = 2)

        assertEquals(VisitNotification.SyncFailed(2), result)
    }

    @Test
    fun everythingSentReportsCompletion() {
        val result = syncResultNotification(synchronized = 3, remaining = 0)

        assertEquals(VisitNotification.SyncCompleted(3), result)
    }

    @Test
    fun nothingToSendClearsTheNotification() {
        assertNull(syncResultNotification(synchronized = 0, remaining = 0))
    }
}
