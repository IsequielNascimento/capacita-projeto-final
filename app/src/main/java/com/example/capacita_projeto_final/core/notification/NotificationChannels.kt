package com.example.capacita_projeto_final.core.notification

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.example.capacita_projeto_final.R

object NotificationChannels {
    const val Visits = "visits"

    fun register(context: Context) {
        val channel = NotificationChannelCompat.Builder(Visits, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(context.getString(R.string.notification_channel_visits_name))
            .setDescription(context.getString(R.string.notification_channel_visits_description))
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}
