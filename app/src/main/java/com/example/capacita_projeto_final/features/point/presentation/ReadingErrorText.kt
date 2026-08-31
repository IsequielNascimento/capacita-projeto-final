package com.example.capacita_projeto_final.features.point.presentation

import androidx.annotation.StringRes
import com.example.capacita_projeto_final.R
import com.example.capacita_projeto_final.features.visit.domain.ReadingError

@StringRes
fun ReadingError.messageRes(): Int = when (this) {
    ReadingError.NotANumber -> R.string.reading_error_not_a_number
    ReadingError.BelowPreviousReading -> R.string.reading_error_below_previous
}
