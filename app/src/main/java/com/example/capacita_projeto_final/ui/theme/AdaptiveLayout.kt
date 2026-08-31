package com.example.capacita_projeto_final.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// MARK: - Size classes

enum class HigWidthClass { Compact, Regular }

@Composable
fun higWidthClass(): HigWidthClass =
    if (LocalConfiguration.current.screenWidthDp >= 600) HigWidthClass.Regular else HigWidthClass.Compact

// MARK: - Readable content width

private val ReadableContentWidth = 640.dp

@Composable
fun rememberReadableContentPadding(bottom: Dp = HigMetrics.groupSpacing): PaddingValues {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    return remember(screenWidth, bottom) {
        val horizontal = if (screenWidth > ReadableContentWidth) {
            (screenWidth - ReadableContentWidth) / 2 + HigMetrics.contentMargin
        } else {
            HigMetrics.contentMargin
        }
        PaddingValues(start = horizontal, end = horizontal, bottom = bottom)
    }
}

@Composable
fun rememberMapHeight(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    return remember(screenHeight) {
        val proportional = screenHeight * 0.45f
        proportional.coerceIn(200.dp, 360.dp)
    }
}
