package com.example.capacita_projeto_final.core

import android.content.Context
import androidx.room.Room
import com.example.capacita_projeto_final.core.database.CapacitaDatabase
import com.example.capacita_projeto_final.features.route.data.RouteRepository

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        CapacitaDatabase::class.java,
        "capacita_field_ops.db",
    ).build()

    val routeRepository = RouteRepository(database.routePointDao())
}
