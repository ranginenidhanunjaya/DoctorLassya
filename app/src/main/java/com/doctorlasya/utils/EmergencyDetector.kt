package com.doctorlasya.utils

import com.doctorlasya.data.models.EmergencyLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyDetector @Inject constructor() {

    // 🚨 CRITICAL — Call 108 immediately
    private val criticalKeywords = setOf(
        // Telugu
        "గుండె నొప్పి", "గుండె ఆగిపోయింది", "శ్వాస అందడం లేదు",
        "స్పృహ తప్పింది", "స్పృహ పోయింది", "మూర్ఛ వచ్చింది",
        "రక్తం ఆగడం లేదు", "చాలా రక్తం పోతోంది", "విషం తిన్నాను",
        "విషం తాగాను", "పక్షవాతం", "స్ట్రోక్", "అపస్మారం",
        "నాలుక పడిపోతోంది", "మింగడం కష్టంగా ఉంది",
        // Telangana dialect
        "గుండె సల్లాల్లే ఉంది", "ఊపిరి అందడం లేదు",
        // Rayalaseema
        "ప్రాణం పోతాందే", "తల తిరుగుతాందే",
        // English (medical emergency terms)
        "heart attack", "chest pain", "cant breathe", "unconscious",
        "stroke", "seizure", "overdose", "severe bleeding",
        "accident", "fell down", "not breathing"
    )

    // ⚠️ MODERATE — See doctor today
    private val moderateKeywords = setOf(
        "చాలా జ్వరం", "103 జ్వరం", "104 జ్వరం",
        "వాంతులు ఆగడం లేదు", "డయేరియా తీవ్రంగా",
        "తలనొప్పి చాలా తీవ్రంగా", "చెవుల్లో శబ్దం",
        "కళ్ళు అస్తమానం తిరుగుతున్నాయి",
        "high fever", "severe headache", "persistent vomiting"
    )

    fun analyze(userText: String): EmergencyLevel {
        val text = userText.lowercase()
        return when {
            criticalKeywords.any { text.contains(it.lowercase()) } -> EmergencyLevel.CRITICAL
            moderateKeywords.any { text.contains(it.lowercase()) } -> EmergencyLevel.MODERATE
            else -> EmergencyLevel.NORMAL
        }
    }

    fun analyzeResponse(llmResponse: String): EmergencyLevel {
        return when {
            llmResponse.contains("[LAASYA_EMERGENCY_108]") -> EmergencyLevel.CRITICAL
            else -> EmergencyLevel.NORMAL
        }
    }
}
