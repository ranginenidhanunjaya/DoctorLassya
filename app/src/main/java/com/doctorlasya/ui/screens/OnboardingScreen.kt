package com.doctorlasya.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.doctorlasya.ui.theme.LaasyaColors

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            emoji    = "🌸",
            title    = "డాక్టర్ లాస్యకు స్వాగతం",
            subtitle = "మీ అరచేతిలో మీ స్వంత ఆరోగ్య నేస్తం.\nతెలుగులో మాట్లాడండి, లాస్య సహాయం చేస్తుంది.",
            icon     = Icons.Default.Favorite
        ),
        OnboardingPage(
            emoji    = "🎙️",
            title    = "\"హే లాస్యా\" అని చెప్పండి",
            subtitle = "మీ ఫోన్ పట్టుకోకుండా కూడా లాస్యతో మాట్లాడవచ్చు.\nWake word ద్వారా instantly మొదలవుతుంది.",
            icon     = Icons.Default.Mic
        ),
        OnboardingPage(
            emoji    = "📷",
            title    = "గాయం చూపించండి",
            subtitle = "చర్మ సమస్య లేదా గాయం ఉంటే\nకెమెరా ద్వారా లాస్య చూసి సలహా ఇస్తుంది.",
            icon     = Icons.Default.CameraAlt
        ),
        OnboardingPage(
            emoji    = "🚨",
            title    = "అత్యవసర సహాయం",
            subtitle = "తీవ్రమైన పరిస్థితిలో లాస్య వెంటనే\n108 కి కనెక్ట్ చేస్తుంది.",
            icon     = Icons.Default.Emergency
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LaasyaColors.Pink60,
                        Color(0xFFC2185B),
                        LaasyaColors.Pink40
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onFinished) {
                    Text("Skip", color = Color.White.copy(alpha = 0.7f))
                }
            }

            // Page content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                },
                label = "onboarding"
            ) { page ->
                val data = pages[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(data.emoji, fontSize = 72.sp)

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(data.icon, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }

                    Text(
                        data.title,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 26.sp,
                        color      = Color.White,
                        textAlign  = TextAlign.Center
                    )
                    Text(
                        data.subtitle,
                        fontSize  = 16.sp,
                        color     = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            // Bottom controls
            Column(
                horizontalAlignment  = Alignment.CenterHorizontally,
                verticalArrangement  = Arrangement.spacedBy(24.dp)
            ) {
                // Page indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (i == currentPage) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (i == currentPage) Color.White
                                    else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape  = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor   = LaasyaColors.Pink60
                    )
                ) {
                    Text(
                        text       = if (currentPage < pages.size - 1) "తర్వాత →" else "🌸 ప్రారంభించండి!",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
                    )
                }
            }
        }
    }
}
