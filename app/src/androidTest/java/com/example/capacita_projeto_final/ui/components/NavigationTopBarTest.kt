package com.example.capacita_projeto_final.ui.components

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.CapacitaTheme
import org.junit.Rule
import org.junit.Test

class NavigationTopBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun backButtonUsesAccessibleTouchTarget() {
        composeTestRule.setContent {
            CapacitaTheme {
                NavigationTopBar(title = "Mapa da rota", onBack = {})
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Voltar")
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
    }
}
