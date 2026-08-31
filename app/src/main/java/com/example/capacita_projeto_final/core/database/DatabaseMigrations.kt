package com.example.capacita_projeto_final.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration1To2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS visits (
                id TEXT NOT NULL PRIMARY KEY,
                pointId INTEGER NOT NULL,
                installationCode TEXT NOT NULL,
                meterNumber TEXT NOT NULL,
                previousReading INTEGER NOT NULL,
                currentReading INTEGER NOT NULL,
                photoUri TEXT,
                latitude REAL,
                longitude REAL,
                capturedAt INTEGER NOT NULL,
                syncStatus TEXT NOT NULL,
                FOREIGN KEY(pointId) REFERENCES route_points(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_visits_pointId ON visits(pointId)")
    }
}
