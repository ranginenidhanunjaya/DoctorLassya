package com.doctorlasya

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class LaasyaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 🌿 Logging — debug only
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // 🔔 Notification channels
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // Wake word service channel
        val wakeWordChannel = NotificationChannel(
            CHANNEL_WAKE_WORD,
            "డాక్టర్ లాస్య వింటోంది",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "హే లాస్యా wake word detection service"
            setShowBadge(false)
        }

        // Emergency alert channel (HIGH priority)
        val emergencyChannel = NotificationChannel(
            CHANNEL_EMERGENCY,
            "🚨 అత్యవసర హెచ్చరిక",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Doctor Laasya emergency alerts"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannels(
            listOf(wakeWordChannel, emergencyChannel)
        )
    }

    companion object {
        const val CHANNEL_WAKE_WORD = "laasya_wake_word"
        const val CHANNEL_EMERGENCY = "laasya_emergency"
    }
}
