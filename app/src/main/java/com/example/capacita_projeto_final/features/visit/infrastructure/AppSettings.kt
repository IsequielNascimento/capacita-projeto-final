package com.example.capacita_projeto_final.features.visit.infrastructure

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object AppSettings {
    fun open(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        runCatching { context.startActivity(intent) }
    }
}
