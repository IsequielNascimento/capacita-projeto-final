package com.example.capacita_projeto_final.features.visit.presentation

import androidx.annotation.StringRes
import com.example.capacita_projeto_final.R

enum class EvidenceFeedback(val isFailure: Boolean, @param:StringRes val messageRes: Int) {
    PhotoAttached(false, R.string.evidence_photo_attached),
    PhotoNotCaptured(true, R.string.evidence_photo_not_captured),
    PhotoStorageUnavailable(true, R.string.evidence_photo_storage),
    CameraPermissionDenied(true, R.string.evidence_camera_denied),
    LocationAttached(false, R.string.evidence_location_attached),
    LocationUnavailable(true, R.string.evidence_location_unavailable),
    LocationPermissionDenied(true, R.string.evidence_location_denied),
}
