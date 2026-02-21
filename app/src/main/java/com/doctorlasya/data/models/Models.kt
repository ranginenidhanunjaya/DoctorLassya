package com.doctorlasya.data.models

import kotlinx.serialization.Serializable

// ─── Chat ───────────────────────────────────────────────
data class ChatMessage(
    val id: String,
    val text: String,
    val sender: Sender,
    val showCameraButton: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Sender { USER, LAASYA }

// ─── Emergency ──────────────────────────────────────────
enum class EmergencyLevel { NORMAL, MODERATE, CRITICAL }

// ─── Gemini API ─────────────────────────────────────────
@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GenerationConfig = GenerationConfig()
)

@Serializable
data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String  // Base64
)

@Serializable
data class GenerationConfig(
    val maxOutputTokens: Int    = 300,
    val temperature: Float      = 0.7f,
    val topP: Float             = 0.9f
)

@Serializable
data class GeminiStreamChunk(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null
)

// ─── ElevenLabs TTS ─────────────────────────────────────
@Serializable
data class ElevenLabsTTSRequest(
    val text: String,
    val model_id: String = "eleven_multilingual_v2",
    val voice_settings: VoiceSettings = VoiceSettings()
)

@Serializable
data class VoiceSettings(
    val stability: Float        = 0.78f,
    val similarity_boost: Float = 0.82f,
    val style: Float            = 0.15f,
    val use_speaker_boost: Boolean = true
)

// ─── User Preferences ───────────────────────────────────
data class LaasyaUserPrefs(
    val userName: String              = "",
    val preferredDialect: TeluguDialect = TeluguDialect.HYDERABAD,
    val wakeWordEnabled: Boolean      = true,
    val ttsEnabled: Boolean           = true,
    val emergencyContact: String      = ""
)

enum class TeluguDialect(val displayName: String) {
    HYDERABAD("హైదరాబాద్ / తెలంగాణ"),
    RAYALASEEMA("రాయలసీమ"),
    COASTAL_AP("కోస్తా ఆంధ్ర")
}
