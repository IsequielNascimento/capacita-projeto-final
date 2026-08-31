package com.example.capacita_projeto_final.features.visit.infrastructure

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
)

class DeviceLocationProvider(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun currentLocation(): DeviceLocation = suspendCancellableCoroutine { continuation ->
        val cancellation = CancellationTokenSource()
        continuation.invokeOnCancellation { cancellation.cancel() }

        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellation.token)
            .addOnSuccessListener { location ->
                if (location == null) {
                    continuation.resumeWithException(IllegalStateException("Localização indisponível"))
                } else {
                    continuation.resume(DeviceLocation(location.latitude, location.longitude))
                }
            }
            .addOnFailureListener(continuation::resumeWithException)
    }
}
