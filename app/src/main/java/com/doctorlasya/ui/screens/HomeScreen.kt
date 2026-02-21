package com.doctorlasya.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doctorlasya.ui.theme.LaasyaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartChat: () -> Unit,
    onSettings: () -> Unit
) {
    var isListening by remember { mutableStateOf(false) }

    // Pulsing animation for the mic button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = if (isListening) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "డాక్టర్ లాస్య 🌸",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 20.sp,
                            color      = Color.White
                        )
                        Text(
                            text     = "మీ ఆరోగ్యమే నా బాధ్యత",
                            fontSize = 12.sp,
                            color    = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LaasyaColors.Pink60
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            LaasyaColors.BlushWhite,
                            Color(0xFFFCE4EC),
                            LaasyaColors.BlushWhite
                        )
                    )
                )
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(24.dp)
            ) {

                // 🌸 Greeting Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = "నమస్కారమండి! 🙏",
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color      = LaasyaColors.Pink60
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text      = "నేను డాక్టర్ లాస్యని అండి.\nమీ ఆరోగ్య సమస్య చెప్పండి,\nనేను సహాయం చేస్తాను.",
                            fontSize  = 15.sp,
                            textAlign = TextAlign.Center,
                            color     = LaasyaColors.TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }

                // 🎙️ Voice Button (BIG, centre-stage)
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsing ring
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(LaasyaColors.Pink60.copy(alpha = 0.15f))
                        )
                    }

                    // Inner ring
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) LaasyaColors.Pink60.copy(alpha = 0.25f)
                                else Color(0xFFFCE4EC)
                            )
                    )

                    // Mic button
                    FloatingActionButton(
                        onClick            = {
                            isListening = !isListening
                            if (!isListening) onStartChat()
                        },
                        modifier           = Modifier.size(100.dp),
                        shape              = CircleShape,
                        containerColor     = LaasyaColors.Pink60,
                        contentColor       = Color.White,
                        elevation          = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            modifier   = Modifier.size(40.dp)
                        )
                    }
                }

                // Status text
                AnimatedContent(
                    targetState = isListening,
                    label       = "statusText"
                ) { listening ->
                    Text(
                        text      = if (listening) "🎙️ వింటున్నాను అండి..." else "నొక్కండి మాట్లాడటానికి",
                        fontSize  = 16.sp,
                        color     = if (listening) LaasyaColors.Pink60 else LaasyaColors.TextSecondary,
                        fontWeight = if (listening) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                // OR type button
                Divider(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    color    = LaasyaColors.Pink60.copy(alpha = 0.3f)
                )

                OutlinedButton(
                    onClick = onStartChat,
                    shape   = RoundedCornerShape(50.dp),
                    border  = ButtonDefaults.outlinedButtonBorder.copy(
                    ),
                    colors  = ButtonDefaults.outlinedButtonColors(
                        contentColor = LaasyaColors.Pink60
                    )
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("టైప్ చేయండి")
                }

                // 🚨 Emergency quick access
                Spacer(Modifier.height(8.dp))
                EmergencyQuickCard()
            }
        }
    }
}

@Composable
fun EmergencyQuickCard() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier            = Modifier.padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "అత్యవసర సహాయం",
                    fontWeight = FontWeight.Bold,
                    color      = LaasyaColors.EmergencyRed,
                    fontSize   = 14.sp
                )
                Text(
                    "తీవ్రమైన పరిస్థితిలో వెంటనే నొక్కండి",
                    fontSize = 12.sp,
                    color    = LaasyaColors.TextSecondary
                )
            }
            Button(
                onClick = { /* Dial 108 */ },
                colors  = ButtonDefaults.buttonColors(
                    containerColor = LaasyaColors.EmergencyRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("108", fontWeight = FontWeight.Bold)
            }
        }
    }
}
