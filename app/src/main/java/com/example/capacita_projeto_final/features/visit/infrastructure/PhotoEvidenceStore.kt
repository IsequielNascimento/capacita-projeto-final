package com.example.capacita_projeto_final.features.visit.infrastructure

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

object PhotoEvidenceStore {
    fun createDestination(context: Context): Uri {
        val root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
        val evidenceDirectory = File(root, "visit-evidence").apply { mkdirs() }
        val photo = File(evidenceDirectory, "evidence-${System.currentTimeMillis()}.jpg").apply {
            createNewFile()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photo,
        )
    }
}
