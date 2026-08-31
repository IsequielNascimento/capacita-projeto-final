package com.example.capacita_projeto_final.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// MARK: - Composition locals

val LocalHigColorScheme = staticCompositionLocalOf { LightHigColorScheme }
val LocalHigTypography = staticCompositionLocalOf { DefaultHigTypography }

object HigTheme {
    val colors: HigColorScheme
        @Composable get() = LocalHigColorScheme.current

    val typography: HigTypography
        @Composable get() = LocalHigTypography.current
}

// MARK: - Theme

private fun HigColorScheme.toMaterialColorScheme() = if (isDark) {
    darkColorScheme(
        primary = accent,
        onPrimary = onAccentFill,
        secondary = accent,
        onSecondary = onAccentFill,
        background = groupedBackground,
        onBackground = label,
        surface = secondaryGroupedBackground,
        onSurface = label,
        surfaceVariant = fill,
        onSurfaceVariant = secondaryLabel,
        outline = separator,
        outlineVariant = separator,
        error = destructive,
        onError = Color.Black,
    )
} else {
    lightColorScheme(
        primary = accent,
        onPrimary = onAccentFill,
        secondary = accent,
        onSecondary = onAccentFill,
        background = groupedBackground,
        onBackground = label,
        surface = secondaryGroupedBackground,
        onSurface = label,
        surfaceVariant = fill,
        onSurfaceVariant = secondaryLabel,
        outline = separator,
        outlineVariant = separator,
        error = destructive,
        onError = Color.White,
    )
}

@Composable
fun CapacitaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val higColors = if (darkTheme) DarkHigColorScheme else LightHigColorScheme
    val higTypography = DefaultHigTypography

    CompositionLocalProvider(
        LocalHigColorScheme provides higColors,
        LocalHigTypography provides higTypography,
        LocalTextStyle provides higTypography.body.copy(color = higColors.label),
    ) {
        MaterialTheme(
            colorScheme = higColors.toMaterialColorScheme(),
            content = content,
        )
    }
}
