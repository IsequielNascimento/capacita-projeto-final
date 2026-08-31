package com.example.capacita_projeto_final.core.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.edit

/**
 * Controla o convite único para receber os avisos de visita salva.
 *
 * Abaixo do Android 13 não existe permissão a pedir, e depois da primeira
 * resposta o convite não volta a aparecer a cada visita.
 */
object NotificationPriming {
    private const val Store = "capacita_notifications"
    private const val KeyPrimingShown = "priming_shown"

    fun shouldPrime(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) return false
        return !store(context).getBoolean(KeyPrimingShown, false)
    }

    fun markPrimed(context: Context) {
        store(context).edit { putBoolean(KeyPrimingShown, true) }
    }

    private fun store(context: Context) =
        context.getSharedPreferences(Store, Context.MODE_PRIVATE)
}
