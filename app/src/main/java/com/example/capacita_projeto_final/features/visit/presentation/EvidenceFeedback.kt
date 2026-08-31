package com.example.capacita_projeto_final.features.visit.presentation

enum class EvidenceFeedback(val isFailure: Boolean) {
    PhotoAttached(false),
    PhotoNotCaptured(true),
    PhotoStorageUnavailable(true),
    CameraPermissionDenied(true),
    LocationAttached(false),
    LocationUnavailable(true),
    LocationPermissionDenied(true);

    fun readableMessage(): String = when (this) {
        PhotoAttached -> "Foto anexada à visita."
        PhotoNotCaptured -> "A foto não foi salva. Tente novamente."
        PhotoStorageUnavailable -> "Não há espaço disponível para guardar a foto."
        CameraPermissionDenied -> "Sem acesso à câmera, a visita é salva sem foto."
        LocationAttached -> "Localização anexada à visita."
        LocationUnavailable -> "Não foi possível obter a localização agora."
        LocationPermissionDenied -> "Sem acesso à localização, a visita é salva sem coordenadas."
    }
}
