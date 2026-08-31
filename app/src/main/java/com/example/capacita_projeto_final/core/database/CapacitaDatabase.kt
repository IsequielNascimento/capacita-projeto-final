package com.example.capacita_projeto_final.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.capacita_projeto_final.features.route.data.local.RoutePointDao
import com.example.capacita_projeto_final.features.route.data.local.RoutePointEntity
import com.example.capacita_projeto_final.features.visit.data.local.VisitDao
import com.example.capacita_projeto_final.features.visit.data.local.VisitEntity

@Database(
    entities = [RoutePointEntity::class, VisitEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class CapacitaDatabase : RoomDatabase() {
    abstract fun routePointDao(): RoutePointDao
    abstract fun visitDao(): VisitDao
}
