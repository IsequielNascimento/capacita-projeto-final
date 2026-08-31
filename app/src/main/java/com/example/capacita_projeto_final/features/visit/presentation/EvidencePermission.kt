package com.example.capacita_projeto_final.features.visit.presentation

import androidx.annotation.StringRes
import com.example.capacita_projeto_final.R

enum class EvidencePermission(
    @StringRes val primingTitleRes: Int,
    @StringRes val primingMessageRes: Int,
    @StringRes val deniedTitleRes: Int,
    @StringRes val deniedMessageRes: Int,
) {
    Camera(
        R.string.permission_camera_title,
        R.string.permission_camera_message,
        R.string.permission_camera_denied_title,
        R.string.permission_camera_denied_message,
    ),
    Location(
        R.string.permission_location_title,
        R.string.permission_location_message,
        R.string.permission_location_denied_title,
        R.string.permission_location_denied_message,
    ),
}
