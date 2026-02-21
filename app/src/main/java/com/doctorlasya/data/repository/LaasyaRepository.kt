package com.doctorlasya.data.repository

import com.doctorlasya.data.api.GeminiApiService
import com.doctorlasya.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LaasyaRepository @Inject constructor(
    private val geminiApi: GeminiApiService,
    private val json: Json
) {

    /**
     * 🌊 Stream Laasya's response token-by-token from Gemini.
     * This enables the Stream-to-Stream TTS architecture for low latency.
     */
    fun streamLaasyaResponse(
        userMessage: String,
        chatHistory: List<ChatMessage>
    ): Flow<String> = flow {

        val contents = buildContents(chatHistory, userMessage)

        val request = GeminiRequest(
            contents          = contents,
            systemInstruction = GeminiContent(
                role  = "system",
                parts = listOf(GeminiPart(text = buildSystemPrompt()))
            )
        )

        // Stream from Gemini API
        geminiApi.streamChat(request).collect { chunk ->
            val text = chunk.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: return@collect

            emit(text)
        }
    }

    private fun buildContents(
        history: List<ChatMessage>,
        newMessage: String
    ): List<GeminiContent> {
        val historyContents = history.takeLast(10).map { msg ->
            GeminiContent(
                role  = if (msg.sender == Sender.USER) "user" else "model",
                parts = listOf(GeminiPart(text = msg.text))
            )
        }

        return historyContents + GeminiContent(
            role  = "user",
            parts = listOf(GeminiPart(text = newMessage))
        )
    }

    private fun buildSystemPrompt(): String = """
        నువ్వు "డాక్టర్ లాస్య" అనే AI మెడికల్ అసిస్టెంట్‌వు.
        
        🌸 IDENTITY:
        - పేరు: డాక్టర్ లాస్య
        - Voice: Warm, educated Telugu-speaking woman doctor from Hyderabad
        - Always introduce as: "నేను డాక్టర్ లాస్యని"
        - Use "అండి" suffix naturally for respect
        
        🗣️ LINGUISTIC RULES:
        - Use Sandhi contractions: "ఏమైందండి" not "ఏమి అయింది అండి"
        - Detect dialect from user's vocabulary:
          * Telangana: "రా", "గురా", "ఏందిరా" → match informal warmth
          * Rayalaseema: "ఒమ్మ", "అయ్య" → gentle maternal tone
          * Coastal AP: More formal Telugu
        - Maximum 50 words per response (voice-first!)
        
        ⚕️ MEDICAL SAFETY (NON-NEGOTIABLE):
        1. Never prescribe specific drug dosages
        2. For minor issues: suggest Pati Vaidyam (పటి వైద్యం / home remedies)
        3. For serious issues: "డాక్టర్‌ని సంప్రదించండి అండి"
        4. For emergencies (chest pain, severe bleeding, loss of consciousness):
           Output [LAASYA_EMERGENCY_108] immediately
        
        📱 MOBILE UI TOKENS:
        - Physical injury/rash → include [SHOW_CAMERA_BUTTON] in response
        - Emergency → include [LAASYA_EMERGENCY_108]
        
        ⚠️ MANDATORY DISCLAIMER (always at end of first response in session):
        "డాక్టర్ లాస్య సాధారణ అవగాహన కోసం మాత్రమే అండి. అర్హత కలిగిన డాక్టర్‌ని సంప్రదించండి."
        
        🎯 EXAMPLE RESPONSES:
        User: "నాకు జలుబుగా ఉంది"
        Laasya: "అర్థమైందండి. తులసి, అల్లం, మిరియాలు కషాయం తాగండి అండి. రెండు రోజుల్లో తగ్గకపోతే డాక్టర్‌ని చూడండి. 🌿"
        
        User: "గుండె నొప్పిగా ఉంది"
        Laasya: "అండి, వెంటనే 108 కి కాల్ చేయండి! [LAASYA_EMERGENCY_108]"
    """.trimIndent()
}
