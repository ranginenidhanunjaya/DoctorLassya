package com.doctorlasya.service

import android.app.*
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ai.picovoice.porcupine.*
import com.doctorlasya.BuildConfig
import com.doctorlasya.LaasyaApp
import com.doctorlasya.R
import com.doctorlasya.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

/**
 * 🎙️ Always-on Foreground Service for "హే లాస్యా" wake word detection.
 * Runs in background with minimal battery impact using Porcupine SDK.
 */
@AndroidEntryPoint
class LaasyaWakeWordService : Service() {

    @Inject
    lateinit var ttsService: LaasyaTTSService

    private var porcupine: Porcupine? = null
    private var audioRecord: AudioRecord? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.doctorlasya.WAKE_WORD_START"
        const val ACTION_STOP  = "com.doctorlasya.WAKE_WORD_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
        initWakeWord()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_STOP -> { stopSelf(); START_NOT_STICKY }
            else        -> START_STICKY
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ─── Wake Word Init ─────────────────────────────────────
    private fun initWakeWord() {
        try {
            porcupine = Porcupine.Builder()
                .setAccessKey(BuildConfig.PORCUPINE_ACCESS_KEY)
                // 🗣️ Custom "హే లాస్యా" wake word model
                // Place your trained .ppn file in assets/
                .setKeywordPath("hey_laasya_telugu.ppn")
                .setSensitivity(0.7f) // Balanced: fewer false positives
                .build(applicationContext)

            startListeningLoop()
            Timber.i("🎙️ Wake word engine started — listening for 'హే లాస్యా'")
        } catch (e: PorcupineException) {
            Timber.e(e, "Porcupine init failed — wake word disabled")
            // App still works without wake word
        }
    }

    private fun startListeningLoop() {
        val engine = porcupine ?: return
        val bufferSize = AudioRecord.getMinBufferSize(
            16000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            16000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        ).also { it.startRecording() }

        serviceScope.launch {
            val buffer = ShortArray(engine.frameLength)
            while (isActive) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: break
                if (read < 0) continue

                try {
                    val keywordIndex = engine.process(buffer)
                    if (keywordIndex >= 0) {
                        // 🌸 "హే లాస్యా" detected!
                        withContext(Dispatchers.Main) {
                            onWakeWordDetected()
                        }
                    }
                } catch (e: PorcupineException) {
                    Timber.e(e, "Porcupine processing error")
                }
            }
        }
    }

    private fun onWakeWordDetected() {
        Timber.d("🌸 Wake word 'హే లాస్యా' detected!")

        // Play activation chime (short, non-intrusive)
        ttsService.speak("చెప్పండి అండి")

        // Open the chat screen
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("OPEN_CHAT", true)
        }
        startActivity(intent)
    }

    // ─── Foreground Notification ─────────────────────────────
    private fun buildNotification(): Notification {
        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, LaasyaWakeWordService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, LaasyaApp.CHANNEL_WAKE_WORD)
            .setSmallIcon(R.drawable.ic_laasya_notification)
            .setContentTitle("డాక్టర్ లాస్య 🌸")
            .setContentText("\"హే లాస్యా\" అని చెప్పి మాట్లాడండి")
            .setContentIntent(openIntent)
            .addAction(0, "ఆపండి", stopIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        porcupine?.delete()
        super.onDestroy()
    }
}
