package com.doctorlasya.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.doctorlasya.BuildConfig
import com.doctorlasya.data.models.ElevenLabsTTSRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LaasyaTTSService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Fallback: Android built-in TTS (Telugu)
    private var androidTts: TextToSpeech? = null
    private var androidTtsReady = false

    // ElevenLabs voice ID for "Doctor Laasya"
    private val voiceId = BuildConfig.ELEVENLABS_VOICE_ID
    private val apiKey  = BuildConfig.ELEVENLABS_API_KEY

    // Audio queue to handle overlapping chunks
    private val audioQueue = ArrayDeque<File>()
    private var isPlaying = false

    init {
        initAndroidTtsFallback()
    }

    /**
     * 🔊 Speak text via ElevenLabs (primary) or Android TTS (fallback).
     * Called with sentence-sized chunks for low latency.
     */
    fun speak(text: String) {
        val cleanText = text
            .replace("[SHOW_CAMERA_BUTTON]", "")
            .replace("[LAASYA_EMERGENCY_108]", "")
            .replace(Regex("<[^>]*>"), "") // strip SSML tags for now
            .trim()

        if (cleanText.isBlank()) return

        scope.launch {
            if (apiKey.isNotBlank() && voiceId.isNotBlank()) {
                speakWithElevenLabs(cleanText)
            } else {
                speakWithAndroidTts(cleanText)
            }
        }
    }

    /**
     * 🎙️ ElevenLabs streaming TTS — premium Laasya voice
     */
    private suspend fun speakWithElevenLabs(text: String) {
        val url = "https://api.elevenlabs.io/v1/text-to-speech/$voiceId/stream"
        val requestBody = ElevenLabsTTSRequest(text = text)
        val bodyJson = json.encodeToString(ElevenLabsTTSRequest.serializer(), requestBody)

        val request = Request.Builder()
            .url(url)
            .addHeader("xi-api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Timber.w("ElevenLabs failed: ${response.code}, falling back to Android TTS")
                    speakWithAndroidTts(text)
                    return
                }

                // Stream audio bytes to temp file, then play
                val audioBytes = response.body?.bytes() ?: return
                val tempFile = File.createTempFile("laasya_tts_", ".mp3", context.cacheDir)
                tempFile.writeBytes(audioBytes)

                playAudioFile(tempFile)
            }
        } catch (e: Exception) {
            Timber.e(e, "ElevenLabs TTS error")
            speakWithAndroidTts(text) // Graceful fallback
        }
    }

    /**
     * 📱 Android built-in TTS — Telugu fallback
     */
    private fun speakWithAndroidTts(text: String) {
        if (!androidTtsReady) return
        androidTts?.speak(text, TextToSpeech.QUEUE_ADD, null, "laasya_${System.currentTimeMillis()}")
    }

    private fun playAudioFile(file: File) {
        audioQueue.addLast(file)
        if (!isPlaying) processQueue()
    }

    private fun processQueue() {
        val file = audioQueue.removeFirstOrNull() ?: run {
            isPlaying = false
            return
        }

        isPlaying = true

        try {
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    file.delete()
                    it.release()
                    processQueue() // Play next chunk
                }
                setOnErrorListener { mp, _, _ ->
                    mp.release()
                    processQueue()
                    true
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "MediaPlayer error")
            processQueue()
        }
    }

    fun stop() {
        audioQueue.clear()
        isPlaying = false
        androidTts?.stop()
    }

    fun setVolume(volume: Float) {
        // Lower volume for emergency state
        androidTts?.setSpeechRate(0.9f)
    }

    private fun initAndroidTtsFallback() {
        androidTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val teluguLocale = Locale("te", "IN")
                val result = androidTts?.setLanguage(teluguLocale)
                androidTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                                   result != TextToSpeech.LANG_NOT_SUPPORTED
                if (!androidTtsReady) {
                    Timber.w("Telugu TTS not available on device, trying default")
                    androidTts?.setLanguage(Locale.getDefault())
                    androidTtsReady = true
                }
                androidTts?.setSpeechRate(0.90f)
                androidTts?.setPitch(1.1f)
            }
        }
    }

    fun shutdown() {
        scope.cancel()
        androidTts?.shutdown()
    }
}
