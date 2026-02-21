package com.doctorlasya.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.doctorlasya.R

// ===========================
// 🎨 DOCTOR LAASYA COLORS
// ===========================
object LaasyaColors {
    // Primary — Warm Pink (లాస్య signature)
    val Pink80          = Color(0xFFF48FB1)
    val Pink60          = Color(0xFFE91E8C)
    val Pink40          = Color(0xFFC2185B)

    // Secondary — Health Green
    val Green80         = Color(0xFFA5D6A7)
    val Green60         = Color(0xFF4CAF50)
    val Green40         = Color(0xFF388E3C)

    // Emergency Red
    val EmergencyRed    = Color(0xFFD32F2F)
    val EmergencyLight  = Color(0xFFFF5252)

    // Neutrals
    val BlushWhite      = Color(0xFFFFF8F9)
    val SoftGray        = Color(0xFFF5F5F5)
    val TextPrimary     = Color(0xFF212121)
    val TextSecondary   = Color(0xFF757575)

    // Voice Waveform
    val WaveActive      = Color(0xFFE91E8C)
    val WaveInactive    = Color(0xFFFFCDD2)
}

private val LightColorScheme = lightColorScheme(
    primary              = LaasyaColors.Pink60,
    onPrimary            = Color.White,
    primaryContainer     = LaasyaColors.Pink80,
    onPrimaryContainer   = LaasyaColors.Pink40,
    secondary            = LaasyaColors.Green60,
    onSecondary          = Color.White,
    secondaryContainer   = LaasyaColors.Green80,
    onSecondaryContainer = LaasyaColors.Green40,
    background           = LaasyaColors.BlushWhite,
    onBackground         = LaasyaColors.TextPrimary,
    surface              = Color.White,
    onSurface            = LaasyaColors.TextPrimary,
    error                = LaasyaColors.EmergencyRed,
    onError              = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary              = LaasyaColors.Pink80,
    onPrimary            = LaasyaColors.Pink40,
    primaryContainer     = LaasyaColors.Pink40,
    onPrimaryContainer   = LaasyaColors.Pink80,
    secondary            = LaasyaColors.Green80,
    onSecondary          = LaasyaColors.Green40,
    background           = Color(0xFF1A0A0F),
    onBackground         = Color(0xFFF5E6EC),
    surface              = Color(0xFF2D1520),
    onSurface            = Color(0xFFF5E6EC),
    error                = LaasyaColors.EmergencyLight,
    onError              = Color.Black,
)

@Composable
fun DoctorLaasyaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography   = LaasyaTypography,
        content      = content
    )
}

// ===========================
// 🔤 TYPOGRAPHY
// ===========================
val LaasyaTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        lineHeight = 40.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    )
)
