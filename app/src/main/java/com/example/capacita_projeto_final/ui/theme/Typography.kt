package com.example.capacita_projeto_final.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// MARK: - Text styles

private fun higStyle(
    size: Int,
    lineHeight: Int,
    tracking: Double,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.sp,
)

@Immutable
data class HigTypography(
    val largeTitle: TextStyle = higStyle(34, 41, 0.37, FontWeight.Bold),
    val title1: TextStyle = higStyle(28, 34, 0.36, FontWeight.Bold),
    val title2: TextStyle = higStyle(22, 28, 0.35, FontWeight.Bold),
    val title3: TextStyle = higStyle(20, 25, 0.38, FontWeight.SemiBold),
    val headline: TextStyle = higStyle(17, 22, -0.41, FontWeight.SemiBold),
    val body: TextStyle = higStyle(17, 22, -0.41),
    val bodyEmphasized: TextStyle = higStyle(17, 22, -0.41, FontWeight.SemiBold),
    val callout: TextStyle = higStyle(16, 21, -0.32),
    val subheadline: TextStyle = higStyle(15, 20, -0.24),
    val subheadlineEmphasized: TextStyle = higStyle(15, 20, -0.24, FontWeight.SemiBold),
    val footnote: TextStyle = higStyle(13, 18, -0.08),
    val footnoteEmphasized: TextStyle = higStyle(13, 18, -0.08, FontWeight.SemiBold),
    val caption1: TextStyle = higStyle(12, 16, 0.0),
    val caption2: TextStyle = higStyle(11, 13, 0.06),
)

val DefaultHigTypography = HigTypography()
