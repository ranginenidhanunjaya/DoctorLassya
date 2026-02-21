package com.doctorlasya.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.doctorlasya.data.models.ChatMessage
import com.doctorlasya.data.models.EmergencyLevel
import com.doctorlasya.data.models.Sender
import com.doctorlasya.ui.components.VoiceWaveform
import com.doctorlasya.ui.theme.LaasyaColors
import com.doctorlasya.ui.viewmodel.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Auto-scroll to latest message
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 🌸 Laasya avatar dot
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💊", fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("డాక్టర్ లాస్య", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Text(
                                text = if (uiState.isListening) "🎙️ వింటున్నాను..." else "ఆన్‌లైన్",
                                fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    // Camera button for injury photos
                    IconButton(onClick = { viewModel.onCameraRequested() }) {
                        Icon(Icons.Default.CameraAlt, "Camera", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LaasyaColors.Pink60)
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText        = uiState.inputText,
                onTextChange     = viewModel::onTextChanged,
                onSend           = { viewModel.sendTextMessage(uiState.inputText) },
                onVoiceToggle    = {
                    if (micPermission.hasPermission) {
                        viewModel.toggleVoiceInput()
                    } else {
                        micPermission.launchPermissionRequest()
                    }
                },
                isListening      = uiState.isListening,
                isLoading        = uiState.isLoading
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            // 🔴 Emergency Overlay — shows instantly on critical keywords
            AnimatedVisibility(
                visible = uiState.emergencyLevel == EmergencyLevel.CRITICAL,
                enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter).zIndex(10f)
            ) {
                EmergencyBanner(
                    onCall108 = {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:108"))
                        context.startActivity(intent)
                    },
                    onDismiss = viewModel::dismissEmergency
                )
            }

            // 💬 Messages list
            LazyColumn(
                state           = listState,
                modifier        = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding  = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }

                // Typing indicator
                if (uiState.isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // 🎙️ Voice waveform overlay when listening
            AnimatedVisibility(
                visible  = uiState.isListening,
                enter    = fadeIn() + slideInVertically(),
                exit     = fadeOut() + slideOutVertically(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                VoiceWaveform()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// 💬 Chat Bubble
// ─────────────────────────────────────────────────────────
@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == Sender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Laasya avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(LaasyaColors.Pink60),
                contentAlignment = Alignment.Center
            ) {
                Text("🌸", fontSize = 14.sp)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Card(
                shape  = RoundedCornerShape(
                    topStart    = if (isUser) 16.dp else 4.dp,
                    topEnd      = if (isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd   = 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) LaasyaColors.Pink60 else Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text     = message.text,
                    color    = if (isUser) Color.White else LaasyaColors.TextPrimary,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 22.sp
                )
            }

            // Camera suggestion button
            if (message.showCameraButton) {
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { /* open camera */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape   = RoundedCornerShape(12.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = LaasyaColors.Pink60)
                ) {
                    Icon(Icons.Default.CameraAlt, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("📷 ఫోటో తీయండి — లాస్య చూస్తుంది", fontSize = 13.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// 🚨 Emergency Banner
// ─────────────────────────────────────────────────────────
@Composable
fun EmergencyBanner(onCall108: () -> Unit, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        color  = LaasyaColors.EmergencyRed,
        shape  = RoundedCornerShape(20.dp),
        shadowElevation = 12.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    "🚨 లాస్య అలర్ట్ — అత్యవసరం!",
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    fontSize   = 18.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "వెంటనే అంబులెన్స్‌కి కాల్ చేయండి అండి",
                color    = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick  = onCall108,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape    = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Call, null, tint = LaasyaColors.EmergencyRed, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("108 కాల్ చేయండి", color = LaasyaColors.EmergencyRed, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                OutlinedButton(
                    onClick  = onDismiss,
                    modifier = Modifier.height(52.dp),
                    border   = BorderStroke(1.dp, Color.White),
                    shape    = RoundedCornerShape(14.dp)
                ) {
                    Text("తర్వాత", color = Color.White)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// ⌨️ Chat Input Bar
// ─────────────────────────────────────────────────────────
@Composable
fun ChatInputBar(
    inputText: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceToggle: () -> Unit,
    isListening: Boolean,
    isLoading: Boolean
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value         = inputText,
                onValueChange = onTextChange,
                modifier      = Modifier.weight(1f),
                placeholder   = { Text("మీ సమస్య చెప్పండి అండి...", fontSize = 14.sp) },
                shape         = RoundedCornerShape(24.dp),
                maxLines      = 3,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = LaasyaColors.Pink60,
                    unfocusedBorderColor = LaasyaColors.Pink60.copy(alpha = 0.4f)
                )
            )

            // 🎙️ Voice toggle
            FilledIconButton(
                onClick  = onVoiceToggle,
                modifier = Modifier.size(48.dp),
                colors   = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isListening) LaasyaColors.EmergencyRed else LaasyaColors.Pink60
                )
            ) {
                Icon(
                    if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = Color.White
                )
            }

            // ➤ Send button
            if (inputText.isNotBlank()) {
                FilledIconButton(
                    onClick  = onSend,
                    enabled  = !isLoading,
                    modifier = Modifier.size(48.dp),
                    colors   = IconButtonDefaults.filledIconButtonColors(containerColor = LaasyaColors.Green60)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Send, "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// 💬 Typing Indicator
// ─────────────────────────────────────────────────────────
@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dot1 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "d1")
    val dot2 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse), label = "d2")
    val dot3 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse), label = "d3")

    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier            = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment   = Alignment.CenterVertically
            ) {
                listOf(dot1, dot2, dot3).forEach { alpha ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LaasyaColors.Pink60.copy(alpha = alpha))
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text("లాస్య అలోచిస్తోంది...", fontSize = 12.sp, color = LaasyaColors.TextSecondary)
            }
        }
    }
}
