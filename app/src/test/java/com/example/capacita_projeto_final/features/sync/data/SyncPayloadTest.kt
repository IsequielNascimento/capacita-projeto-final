package com.example.capacita_projeto_final.features.sync.data

import com.example.capacita_projeto_final.features.visit.domain.SyncStatus
import com.example.capacita_projeto_final.features.visit.domain.Visit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncPayloadTest {
    @Test
    fun mapsVisitToExternalApiPayload() {
        val payload = Visit(
            id = "visit-1",
            pointId = 101,
            installationCode = "CAP-ALD-0001",
            meterNumber = "MED-10001",
            previousReading = 100,
            currentReading = 125,
            photoUri = null,
            latitude = -3.7,
            longitude = -38.5,
            capturedAt = 1234,
            syncStatus = SyncStatus.Pending,
        ).toPayload()

        assertEquals(101, payload.userId)
        assertEquals("Visita CAP-ALD-0001", payload.title)
        assertTrue(payload.body.contains("leituraAtual=125"))
    }
}
