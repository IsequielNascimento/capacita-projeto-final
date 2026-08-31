package com.example.capacita_projeto_final.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// MARK: - Corner radii

@Immutable
object HigRadius {
    val group: Dp = 10.dp
    val control: Dp = 10.dp
    val alert: Dp = 14.dp
    val sheet: Dp = 14.dp
    val badge: Dp = 6.dp
}

@Immutable
object HigShapes {
    val group = RoundedCornerShape(HigRadius.group)
    val control = RoundedCornerShape(HigRadius.control)
    val alert = RoundedCornerShape(HigRadius.alert)
    val sheet = RoundedCornerShape(topStart = HigRadius.sheet, topEnd = HigRadius.sheet)
    val badge = RoundedCornerShape(HigRadius.badge)
}

fun concentricRadius(outer: Dp, padding: Dp): Dp = (outer - padding).coerceAtLeast(0.dp)
