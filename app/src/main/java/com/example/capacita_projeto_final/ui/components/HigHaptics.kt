package com.example.capacita_projeto_final.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

// MARK: - Outcome feedback

enum class HigHapticOutcome { Success, Failure }

@Composable
fun HigHapticFeedback(outcome: HigHapticOutcome?, key: Any?) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(key, outcome) {
        when (outcome) {
            HigHapticOutcome.Success -> haptics.performHapticFeedback(HapticFeedbackType.Confirm)
            HigHapticOutcome.Failure -> haptics.performHapticFeedback(HapticFeedbackType.Reject)
            null -> Unit
        }
    }
}
