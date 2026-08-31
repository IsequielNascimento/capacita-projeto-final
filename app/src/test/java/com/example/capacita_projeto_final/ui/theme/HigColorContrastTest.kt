package com.example.capacita_projeto_final.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

class HigColorContrastTest {

    @Test
    fun textColorsMeetMinimumContrastInLightScheme() {
        assertTextPairs(LightHigColorScheme)
    }

    @Test
    fun textColorsMeetMinimumContrastInDarkScheme() {
        assertTextPairs(DarkHigColorScheme)
    }

    @Test
    fun mapGraphicsMeetMinimumContrastInBothSchemes() {
        listOf(LightHigColorScheme, DarkHigColorScheme).forEach { scheme ->
            assertContrast(scheme.mapStreet, scheme.mapBackground, GRAPHIC_MINIMUM, "mapStreet")
            assertContrast(scheme.accentFill, scheme.mapBackground, GRAPHIC_MINIMUM, "routeStroke")
            assertContrast(scheme.successFill, scheme.mapBackground, GRAPHIC_MINIMUM, "visitedMarker")
        }
    }

    private fun assertTextPairs(scheme: HigColorScheme) {
        val backgrounds = listOf(
            scheme.secondaryGroupedBackground to "secondaryGroupedBackground",
            scheme.groupedBackground to "groupedBackground",
            scheme.barBackground to "barBackground",
        )
        val foregrounds = listOf(
            scheme.label to "label",
            scheme.secondaryLabel to "secondaryLabel",
            scheme.accent to "accent",
            scheme.success to "success",
            scheme.warning to "warning",
            scheme.destructive to "destructive",
        )
        backgrounds.forEach { (background, backgroundName) ->
            foregrounds.forEach { (foreground, foregroundName) ->
                assertContrast(foreground, background, TEXT_MINIMUM, "$foregroundName on $backgroundName")
            }
        }
        assertContrast(scheme.onAccentFill, scheme.accentFill, TEXT_MINIMUM, "onAccentFill on accentFill")
        assertContrast(scheme.onSuccessFill, scheme.successFill, TEXT_MINIMUM, "onSuccessFill on successFill")
    }

    private fun assertContrast(foreground: Color, background: Color, minimum: Double, label: String) {
        val ratio = contrastRatio(foreground, background)
        assertTrue(
            "$label has contrast %.2f:1, below the required %.1f:1".format(ratio, minimum),
            ratio >= minimum,
        )
    }

    private fun contrastRatio(foreground: Color, background: Color): Double {
        val first = relativeLuminance(foreground)
        val second = relativeLuminance(background)
        return (max(first, second) + 0.05) / (min(first, second) + 0.05)
    }

    private fun relativeLuminance(color: Color): Double {
        fun channel(value: Float): Double {
            val normalized = value.toDouble()
            return if (normalized <= 0.03928) normalized / 12.92 else ((normalized + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
    }

    private companion object {
        const val TEXT_MINIMUM = 4.5
        const val GRAPHIC_MINIMUM = 3.0
    }
}
