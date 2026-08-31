package com.example.capacita_projeto_final.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.capacita_projeto_final.features.route.data.local.RoutePointDao
import com.example.capacita_projeto_final.features.route.data.local.RoutePointEntity

@Database(
    entities = [RoutePointEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CapacitaDatabase : RoomDatabase() {
    abstract fun routePointDao(): RoutePointDao
}
