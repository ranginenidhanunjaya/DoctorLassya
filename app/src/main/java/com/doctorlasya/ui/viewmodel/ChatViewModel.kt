package com.doctorlasya.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doctorlasya.data.models.*
import com.doctorlasya.data.repository.LaasyaRepository
import com.doctorlasya.service.LaasyaTTSService
import com.doctorlasya.service.SpeechRecognitionManager
import com.doctorlasya.utils.EmergencyDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage>  = emptyList(),
    val inputText: String            = "",
    val isListening: Boolean         = false,
    val isLoading: Boolean           = false,
    val emergencyLevel: EmergencyLevel = EmergencyLevel.NORMAL,
    val showCameraButton: Boolean    = false,
    val error: String?               = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: LaasyaRepository,
    private val ttsService: LaasyaTTSService,
    private val speechManager: SpeechRecognitionManager,
    private val emergencyDetector: EmergencyDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        // 🌸 Laasya greets the user on launch
        addLaasyaMessage(
            text = "నమస్కారమండి! నేను డాక్టర్ లాస్యని. " +
                   "మీకు ఏం సమస్యగా ఉందో చెప్పండి అండి. 🌸"
        )

        // Listen for speech results
        viewModelScope.launch {
            speechManager.recognizedText.collect { spokenText ->
                if (spokenText.isNotBlank()) {
                    sendTextMessage(spokenText)
                }
            }
        }
    }

    fun onTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendTextMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id     = UUID.randomUUID().toString(),
            text   = text.trim(),
            sender = Sender.USER
        )

        _uiState.update {
            it.copy(
                messages  = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                isListening = false
            )
        }

        // 🚨 Check emergency INSTANTLY before API call
        val emergencyLevel = emergencyDetector.analyze(text)
        if (emergencyLevel == EmergencyLevel.CRITICAL) {
            _uiState.update { it.copy(emergencyLevel = EmergencyLevel.CRITICAL) }
        }

        viewModelScope.launch {
            // Stream response from Gemini
            val responseBuffer = StringBuilder()
            var isFirstChunk = true
            var messageId = UUID.randomUUID().toString()

            repository.streamLaasyaResponse(
                userMessage    = text,
                chatHistory    = _uiState.value.messages
            ).collect { chunk ->

                responseBuffer.append(chunk)

                if (isFirstChunk) {
                    // Add initial empty message that will be updated
                    addLaasyaMessage(text = chunk, id = messageId)
                    isFirstChunk = false
                    // 🔊 Start speaking the first chunk immediately (low latency!)
                    ttsService.speak(chunk)
                } else {
                    // Update existing message with streamed text
                    updateLaasyaMessage(messageId, responseBuffer.toString())
                }

                // Speak in sentence-sized chunks
                if (chunk.containsNaturalPause() && chunk.length > 5) {
                    ttsService.speak(chunk)
                }
            }

            val finalResponse = responseBuffer.toString()
            val showCamera = finalResponse.contains("[SHOW_CAMERA_BUTTON]")
            val isEmergency = finalResponse.contains("[LAASYA_EMERGENCY_108]")

            _uiState.update { state ->
                state.copy(
                    isLoading      = false,
                    emergencyLevel = if (isEmergency) EmergencyLevel.CRITICAL else state.emergencyLevel,
                    messages       = state.messages.map { msg ->
                        if (msg.id == messageId) {
                            msg.copy(
                                text             = finalResponse
                                    .replace("[SHOW_CAMERA_BUTTON]", "")
                                    .replace("[LAASYA_EMERGENCY_108]", "")
                                    .trim(),
                                showCameraButton = showCamera
                            )
                        } else msg
                    }
                )
            }
        }
    }

    fun toggleVoiceInput() {
        val currentlyListening = _uiState.value.isListening
        if (currentlyListening) {
            speechManager.stopListening()
            _uiState.update { it.copy(isListening = false) }
        } else {
            speechManager.startListening()
            _uiState.update { it.copy(isListening = true) }
        }
    }

    fun onCameraRequested() {
        // Navigate to camera — handled by UI layer
        _uiState.update { it.copy(showCameraButton = true) }
    }

    fun dismissEmergency() {
        _uiState.update { it.copy(emergencyLevel = EmergencyLevel.NORMAL) }
    }

    // ─── Helpers ───────────────────────────────────────────

    private fun addLaasyaMessage(text: String, id: String = UUID.randomUUID().toString()) {
        val message = ChatMessage(id = id, text = text, sender = Sender.LAASYA)
        _uiState.update { it.copy(messages = it.messages + message) }
    }

    private fun updateLaasyaMessage(id: String, text: String) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map {
                    if (it.id == id) it.copy(text = text) else it
                }
            )
        }
    }

    private fun String.containsNaturalPause(): Boolean {
        return endsWith('.') || endsWith('?') || endsWith(',') ||
               endsWith('!') || endsWith('।') || length > 80
    }
}
