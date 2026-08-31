package com.example.capacita_projeto_final.features.visit.presentation

import androidx.annotation.StringRes
import com.example.capacita_projeto_final.R

enum class EvidencePermission(
    @param:StringRes val primingTitleRes: Int,
    @param:StringRes val primingMessageRes: Int,
    @param:StringRes val deniedTitleRes: Int,
    @param:StringRes val deniedMessageRes: Int,
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
    Notifications(
        R.string.permission_notifications_title,
        R.string.permission_notifications_message,
        R.string.permission_notifications_denied_title,
        R.string.permission_notifications_denied_message,
    ),
}
