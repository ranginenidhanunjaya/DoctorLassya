# Doctor Laasya ProGuard Rules

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.doctorlasya.data.models.**$$serializer { *; }
-keepclassmembers class com.doctorlasya.data.models.** { *** Companion; }

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Porcupine
-keep class ai.picovoice.** { *; }

# Timber
-dontwarn org.jetbrains.annotations.**
