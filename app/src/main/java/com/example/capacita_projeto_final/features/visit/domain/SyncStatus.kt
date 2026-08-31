package com.example.capacita_projeto_final.features.visit.domain

enum class SyncStatus(val storageValue: String) {
    Pending("pending"),
    Sending("syncing"),
    Sent("synced"),
    Failed("error");

    companion object {
        fun fromStorage(value: String): SyncStatus =
            entries.firstOrNull { it.storageValue == value } ?: Pending
    }
}
