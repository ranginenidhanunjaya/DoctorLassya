# 🌸 డాక్టర్ లాస్య — Doctor Laasya Android App

> **"మీ ఆరోగ్యమే నా బాధ్యత"**  
> Your personal Telugu AI Health Assistant

---

## 📱 App Overview

Doctor Laasya is a voice-first, Telugu-language AI medical assistant app for Android. Built with Jetpack Compose, it integrates Gemini 2.0 Flash for AI responses, ElevenLabs for a warm Telugu voice, and Porcupine for "హే లాస్యా" wake word detection.

---

## 🗂️ Project Structure

```
DoctorLaasya/
├── app/src/main/java/com/doctorlasya/
│   ├── LaasyaApp.kt                    # Application class + Hilt entry
│   ├── data/
│   │   ├── api/
│   │   │   └── GeminiApiService.kt     # Streaming Gemini API client
│   │   ├── models/
│   │   │   └── Models.kt               # All data classes
│   │   └── repository/
│   │       └── LaasyaRepository.kt     # AI prompt + streaming logic
│   ├── di/
│   │   └── AppModule.kt               # Hilt dependency injection
│   ├── service/
│   │   ├── LaasyaTTSService.kt        # ElevenLabs + Android TTS
│   │   ├── LaasyaWakeWordService.kt   # "హే లాస్యా" always-on detection
│   │   └── SpeechRecognitionManager.kt # Telugu STT
│   ├── ui/
│   │   ├── MainActivity.kt
│   │   ├── navigation/NavGraph.kt
│   │   ├── screens/
│   │   │   ├── HomeScreen.kt          # Main home with voice button
│   │   │   ├── ChatScreen.kt          # Conversation + emergency UI
│   │   │   ├── OnboardingScreen.kt    # 4-page onboarding
│   │   │   └── SettingsScreen.kt      # Dialect, voice, emergency contact
│   │   ├── components/
│   │   │   └── VoiceWaveform.kt       # Animated waveform visualizer
│   │   ├── theme/Theme.kt             # Laasya brand colors + typography
│   │   └── viewmodel/ChatViewModel.kt  # Core state management
│   └── utils/
│       └── EmergencyDetector.kt       # Telugu keyword emergency detection
└── res/
    ├── values/strings.xml             # All Telugu strings
    ├── values/colors.xml
    ├── values/themes.xml
    └── xml/network_security_config.xml
```

---

## 🚀 Quick Start

### 1. Prerequisites
- Android Studio Ladybug 2024.2+
- JDK 17
- Android device / emulator (API 26+)

### 2. API Keys Setup
```bash
cp local.properties.template local.properties
# Edit local.properties and add your keys
```

| Key | Where to get |
|-----|-------------|
| `GEMINI_API_KEY` | [Google AI Studio](https://aistudio.google.com/) |
| `ELEVENLABS_API_KEY` | [ElevenLabs](https://elevenlabs.io/) |
| `ELEVENLABS_VOICE_ID` | Create "Laasya" voice in Voice Lab (see below) |
| `PORCUPINE_ACCESS_KEY` | [Picovoice Console](https://console.picovoice.ai/) |

### 3. ElevenLabs Voice Design (Doctor Laasya)
1. Go to ElevenLabs → Voice Lab → **Voice Design**
2. Settings:
   - Gender: Female
   - Age: Young Adult  
   - Accent: Indian (Telugu)
   - Style Exaggeration: 15%
3. Name it "Doctor Laasya"
4. Copy Voice ID → `local.properties`

### 4. Wake Word Setup
1. Go to [Picovoice Console](https://console.picovoice.ai/)
2. Create wake word: **"Hey Laasya"** (Telugu accent)
3. Download `.ppn` file
4. Place at: `app/src/main/assets/hey_laasya_telugu.ppn`

### 5. Build & Run
```bash
./gradlew assembleDebug
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    DOCTOR LAASYA                            │
│                                                             │
│  "హే లాస్యా"  ──▶  WakeWordService  ──▶  MainActivity       │
│                                                             │
│   User Voice  ──▶  SpeechRecognizer  ──▶  ChatViewModel    │
│                                               │             │
│                                         LaasyaRepository    │
│                                               │             │
│                                        GeminiApiService     │
│                                        (Streaming SSE)      │
│                                               │             │
│                                         LaasyaTTSService    │
│                                    (ElevenLabs streaming)   │
└─────────────────────────────────────────────────────────────┘
```

**Key Design Principle:** Stream-to-Stream architecture.  
As Gemini generates Telugu text → immediately sent to ElevenLabs → audio plays before the full response is done. Target latency: **< 800ms**.

---

## 🔑 Key Features

| Feature | Implementation |
|---------|---------------|
| 🎙️ Wake Word | Porcupine "హే లాస్యా" — `LaasyaWakeWordService` |
| 🌊 Streaming AI | Gemini 2.0 Flash SSE — `GeminiApiService` |
| 🔊 Telugu Voice | ElevenLabs Multilingual v2 — `LaasyaTTSService` |
| 🚨 Emergency | Auto-detect → 108 button — `EmergencyDetector` |
| 📷 Camera | Injury/rash analysis — Gemini Vision |
| 🗣️ Dialect | Telangana / Rayalaseema / Coastal AP |

---

## ⚠️ Medical Disclaimer

> **డాక్టర్ లాస్య అందించే సమాచారం సాధారణ అవగాహన కోసం మాత్రమే అండి.**  
> This app does not provide medical diagnosis. Always consult a qualified doctor.

---

## 📄 License

```
Copyright 2026 Doctor Laasya

Licensed under the Apache License, Version 2.0
```

---

*మీ ఆరోగ్యమే నా బాధ్యత 🌸 — Doctor Laasya*
