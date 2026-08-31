package com.example.capacita_projeto_final.core

import android.content.Context
import androidx.room.Room
import com.example.capacita_projeto_final.core.database.CapacitaDatabase
import com.example.capacita_projeto_final.core.database.Migration1To2
import com.example.capacita_projeto_final.features.route.data.RouteRepository
import com.example.capacita_projeto_final.features.sync.data.SyncRepository
import com.example.capacita_projeto_final.features.sync.data.remote.CapacitaApi
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import com.example.capacita_projeto_final.features.visit.infrastructure.DeviceLocationProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        CapacitaDatabase::class.java,
        "capacita_projeto_final.db",
    ).addMigrations(Migration1To2).build()

    val routeRepository = RouteRepository(database.routePointDao())
    val visitRepository = VisitRepository(database.visitDao())

    private val capacitaApi = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CapacitaApi::class.java)

    val syncRepository = SyncRepository(capacitaApi, visitRepository)
    val deviceLocationProvider = DeviceLocationProvider(context)
}
