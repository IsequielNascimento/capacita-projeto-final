package com.example.capacita_projeto_final

import android.app.Application
import com.example.capacita_projeto_final.core.AppContainer
import com.example.capacita_projeto_final.core.notification.NotificationChannels

/**
 * Guarda o [AppContainer] no escopo do processo.
 *
 * A Activity e o receiver da notificação compartilham a mesma instância, o que
 * garante um único banco Room aberto sobre o arquivo.
 */
class CapacitaApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.register(this)
    }
}
