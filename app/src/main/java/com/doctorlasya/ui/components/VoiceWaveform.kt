package com.doctorlasya.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.doctorlasya.ui.theme.LaasyaColors

/**
 * 🌊 Animated voice waveform shown while Laasya is listening.
 * 5 bars that animate at different phases, like a real audio visualizer.
 */
@Composable
fun VoiceWaveform(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    // 5 bars with staggered animation phases
    val delays = listOf(0, 150, 300, 150, 0)
    val bars = delays.map { delay ->
        infiniteTransition.animateFloat(
            initialValue  = 0.3f,
            targetValue   = 1.0f,
            animationSpec = infiniteRepeatable(
                animation  = tween(600, delayMillis = delay, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$delay"
        )
    }

    Box(
        modifier        = modifier
            .fillMaxWidth()
            .background(
                color = LaasyaColors.Pink60.copy(alpha = 0.95f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Waveform bars
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.height(60.dp)
            ) {
                bars.forEach { animatedHeight ->
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight(animatedHeight.value)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text     = "🎙️ వింటున్నాను అండి...",
                color    = Color.White,
                fontSize = 16.sp
            )
            Text(
                text     = "మాట్లాడండి",
                color    = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }
    }
}
