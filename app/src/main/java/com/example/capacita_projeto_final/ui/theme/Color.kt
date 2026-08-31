package com.example.capacita_projeto_final.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// MARK: - Label colors

private val LabelLight = Color(0xFF000000)
private val SecondaryLabelLight = Color(0xFF6B6B70)
private val TertiaryLabelLight = Color(0xFF8A8A8E)

private val LabelDark = Color(0xFFFFFFFF)
private val SecondaryLabelDark = Color(0xFFA1A1A8)
private val TertiaryLabelDark = Color(0xFF8A8A8F)

// MARK: - Tint colors

private val AccentLight = Color(0xFF0060DF)
private val AccentFillLight = Color(0xFF0060DF)
private val AccentDark = Color(0xFF4C9CFF)
private val AccentFillDark = Color(0xFF0A6FE8)

private val SuccessLight = Color(0xFF1B7F3B)
private val SuccessDark = Color(0xFF56D364)
private val WarningLight = Color(0xFF8A5000)
private val WarningDark = Color(0xFFFFB340)
private val DestructiveLight = Color(0xFFD70015)
private val DestructiveDark = Color(0xFFFF6961)

// MARK: - Background colors

private val SystemBackgroundLight = Color(0xFFFFFFFF)
private val GroupedBackgroundLight = Color(0xFFF2F2F7)
private val SecondaryGroupedBackgroundLight = Color(0xFFFFFFFF)
private val BarBackgroundLight = Color(0xFFF9F9F9)
private val SeparatorLight = Color(0xFFC6C6C8)
private val FillLight = Color(0xFFE9E9EF)

private val SystemBackgroundDark = Color(0xFF000000)
private val GroupedBackgroundDark = Color(0xFF000000)
private val SecondaryGroupedBackgroundDark = Color(0xFF1C1C1E)
private val BarBackgroundDark = Color(0xFF1D1D1F)
private val SeparatorDark = Color(0xFF38383A)
private val FillDark = Color(0xFF2C2C2E)

// MARK: - Scheme

@Immutable
data class HigColorScheme(
    val label: Color,
    val secondaryLabel: Color,
    val tertiaryLabel: Color,
    val accent: Color,
    val accentFill: Color,
    val onAccentFill: Color,
    val success: Color,
    val successFill: Color,
    val onSuccessFill: Color,
    val warning: Color,
    val destructive: Color,
    val systemBackground: Color,
    val groupedBackground: Color,
    val secondaryGroupedBackground: Color,
    val barBackground: Color,
    val separator: Color,
    val fill: Color,
    val mapBackground: Color,
    val mapStreet: Color,
    val isDark: Boolean,
)

val LightHigColorScheme = HigColorScheme(
    label = LabelLight,
    secondaryLabel = SecondaryLabelLight,
    tertiaryLabel = TertiaryLabelLight,
    accent = AccentLight,
    accentFill = AccentFillLight,
    onAccentFill = Color.White,
    success = SuccessLight,
    successFill = SuccessLight,
    onSuccessFill = Color.White,
    warning = WarningLight,
    destructive = DestructiveLight,
    systemBackground = SystemBackgroundLight,
    groupedBackground = GroupedBackgroundLight,
    secondaryGroupedBackground = SecondaryGroupedBackgroundLight,
    barBackground = BarBackgroundLight,
    separator = SeparatorLight,
    fill = FillLight,
    mapBackground = GroupedBackgroundLight,
    mapStreet = Color(0xFF8A8A8E),
    isDark = false,
)

val DarkHigColorScheme = HigColorScheme(
    label = LabelDark,
    secondaryLabel = SecondaryLabelDark,
    tertiaryLabel = TertiaryLabelDark,
    accent = AccentDark,
    accentFill = AccentFillDark,
    onAccentFill = Color.White,
    success = SuccessDark,
    successFill = SuccessDark,
    onSuccessFill = Color.Black,
    warning = WarningDark,
    destructive = DestructiveDark,
    systemBackground = SystemBackgroundDark,
    groupedBackground = GroupedBackgroundDark,
    secondaryGroupedBackground = SecondaryGroupedBackgroundDark,
    barBackground = BarBackgroundDark,
    separator = SeparatorDark,
    fill = FillDark,
    mapBackground = SecondaryGroupedBackgroundDark,
    mapStreet = Color(0xFF6E6E73),
    isDark = true,
)
