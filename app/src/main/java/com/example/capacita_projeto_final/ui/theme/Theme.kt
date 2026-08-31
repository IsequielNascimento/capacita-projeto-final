package com.example.capacita_projeto_final.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CapacitaColors = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    secondary = Cyan,
    onSecondary = Color.White,
    background = Mist,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    error = Danger,
)

@Composable
fun CapacitaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CapacitaColors,
        content = content,
    )
}
