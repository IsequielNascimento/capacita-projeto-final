package com.example.capacita_projeto_final.features.visit.presentation

import androidx.annotation.StringRes
import com.example.capacita_projeto_final.R

@StringRes
fun VisitError.messageRes(): Int = when (this) {
    VisitError.PointRemoved -> R.string.visit_error_point_removed
    VisitError.SaveFailed -> R.string.visit_error_save_failed
}
