package com.example.capacita_projeto_final.ui.components

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.CapacitaTheme
import org.junit.Rule
import org.junit.Test

class HigNavigationBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun backControlMeetsMinimumTouchTarget() {
        composeTestRule.setContent {
            CapacitaTheme {
                HigNavigationBar(
                    title = "Mapa da rota",
                    backTitle = "Rota",
                    backAccessibilityLabel = "Voltar para Rota",
                    onBack = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Voltar para Rota")
            .assertWidthIsAtLeast(44.dp)
            .assertHeightIsAtLeast(44.dp)
    }
}
