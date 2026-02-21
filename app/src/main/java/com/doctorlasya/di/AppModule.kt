package com.doctorlasya.di

import android.content.Context
import com.doctorlasya.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient         = true
        encodeDefaults    = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)  // Long timeout for streaming
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY

    @Provides
    @Named("elevenlabs_api_key")
    fun provideElevenLabsApiKey(): String = BuildConfig.ELEVENLABS_API_KEY

    @Provides
    @Named("elevenlabs_voice_id")
    fun provideElevenLabsVoiceId(): String = BuildConfig.ELEVENLABS_VOICE_ID

    @Provides
    @Named("porcupine_key")
    fun providePorcupineKey(): String = BuildConfig.PORCUPINE_ACCESS_KEY
}
