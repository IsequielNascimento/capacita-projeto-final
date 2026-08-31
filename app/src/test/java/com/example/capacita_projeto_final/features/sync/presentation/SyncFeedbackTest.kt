package com.example.capacita_projeto_final.features.sync.presentation

import com.example.capacita_projeto_final.features.sync.data.SyncOutcome
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncFeedbackTest {

    @Test
    fun anyFailureIsReportedAsPartialEvenWithVisitsSent() {
        val feedback = outcome(synchronized = 2, failed = 1).toFeedback()

        assertEquals(SyncFeedback.PartiallyFailed(synchronized = 2, failed = 1), feedback)
    }

    @Test
    fun everyVisitSentIsReportedAsCompleted() {
        val feedback = outcome(synchronized = 3, failed = 0).toFeedback()

        assertEquals(SyncFeedback.Completed(synchronized = 3), feedback)
    }

    @Test
    fun emptyQueueIsNotReportedAsSuccess() {
        val feedback = outcome(synchronized = 0, failed = 0).toFeedback()

        assertEquals(SyncFeedback.NothingPending, feedback)
    }

    private fun outcome(synchronized: Int, failed: Int) = SyncOutcome(
        serviceName = "capacita",
        synchronized = synchronized,
        failed = failed,
    )
}
