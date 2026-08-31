package com.example.capacita_projeto_final.features.visit.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.ui.theme.CapacitaTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VisitScreenDiscardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cancellingWithCapturedEvidenceAsksForConfirmation() {
        var dismissed = false
        composeTestRule.setContent {
            CapacitaTheme {
                VisitScreen(
                    state = readyState(photoUri = "content://photo/1"),
                    onDismiss = { dismissed = true },
                    onSave = {},
                    onFinish = {},
                    onPhotoCaptured = {},
                    onEvidenceMessage = {},
                    onCaptureLocation = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Cancelar").performClick()

        composeTestRule.onNodeWithText("Descartar este registro?").assertIsDisplayed()
        assertFalse("evidence was discarded without confirmation", dismissed)

        composeTestRule.onNodeWithText("Descartar").performClick()
        assertTrue("confirming discard did not dismiss the sheet", dismissed)
    }

    @Test
    fun cancellingWithoutEvidenceDismissesImmediately() {
        var dismissed = false
        composeTestRule.setContent {
            CapacitaTheme {
                VisitScreen(
                    state = readyState(photoUri = null),
                    onDismiss = { dismissed = true },
                    onSave = {},
                    onFinish = {},
                    onPhotoCaptured = {},
                    onEvidenceMessage = {},
                    onCaptureLocation = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Cancelar").performClick()

        assertTrue("cancelling an empty form should close the sheet", dismissed)
    }

    private fun readyState(photoUri: String?) = VisitUiState.Ready(
        point = RoutePoint(
            id = 101,
            order = 1,
            installationCode = "CAP-ALD-0001",
            customer = "Residência 001",
            referencePoint = "Polo de Inovação do IFCE",
            address = "R. Nogueira Acioli, 621",
            latitude = -3.72,
            longitude = -38.51,
            meterNumber = "MED-10001",
            previousReading = 12874,
        ),
        reading = 13010,
        saving = false,
        photoUri = photoUri,
        location = null,
        locationLoading = false,
        feedback = null,
    )
}
