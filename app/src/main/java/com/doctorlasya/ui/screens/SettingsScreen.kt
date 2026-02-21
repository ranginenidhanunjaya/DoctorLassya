package com.doctorlasya.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.doctorlasya.data.models.TeluguDialect
import com.doctorlasya.ui.theme.LaasyaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var wakeWordEnabled by remember { mutableStateOf(true) }
    var ttsEnabled by remember { mutableStateOf(true) }
    var selectedDialect by remember { mutableStateOf(TeluguDialect.HYDERABAD) }
    var emergencyContact by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                title = { Text("సెట్టింగ్స్", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LaasyaColors.Pink60)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Voice Settings
            item {
                SettingsSectionHeader("🎙️ వాయిస్ సెట్టింగ్స్")
            }
            item {
                SettingsToggleCard(
                    icon    = Icons.Default.Mic,
                    title   = "హే లాస్యా వేక్ వర్డ్",
                    subtitle = "\"హే లాస్యా\" అంటే ఆటోమాటిక్‌గా తెరుచుకుంటుంది",
                    checked  = wakeWordEnabled,
                    onToggle = { wakeWordEnabled = it }
                )
            }
            item {
                SettingsToggleCard(
                    icon     = Icons.Default.VolumeUp,
                    title    = "లాస్య వాయిస్",
                    subtitle = "ElevenLabs ద్వారా లాస్య గొంతు వినండి",
                    checked  = ttsEnabled,
                    onToggle = { ttsEnabled = it }
                )
            }

            // Dialect settings
            item { SettingsSectionHeader("🗣️ తెలుగు యాస") }
            item {
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TeluguDialect.values().forEach { dialect ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDialect == dialect,
                                    onClick  = { selectedDialect = dialect },
                                    colors   = RadioButtonDefaults.colors(
                                        selectedColor = LaasyaColors.Pink60
                                    )
                                )
                                Text(
                                    dialect.displayName,
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Emergency Contact
            item { SettingsSectionHeader("🚨 అత్యవసర సంప్రదింపు") }
            item {
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "అత్యవసర పరిస్థితిలో కాల్ చేయవలసిన నంబర్",
                            fontSize = 13.sp,
                            color    = LaasyaColors.TextSecondary
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value         = emergencyContact,
                            onValueChange = { emergencyContact = it },
                            label         = { Text("ఫోన్ నంబర్") },
                            leadingIcon   = { Icon(Icons.Default.Phone, null, tint = LaasyaColors.Pink60) },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(12.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LaasyaColors.Pink60
                            )
                        )
                    }
                }
            }

            // Disclaimer
            item { SettingsSectionHeader("⚠️ వైద్య నిరాకరణ") }
            item {
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text = "డాక్టర్ లాస్య అందించే సమాచారం సాధారణ అవగాహన కోసం మాత్రమే అండి. " +
                               "ఇది లైసెన్స్ పొందిన వైద్యుని సలహాకు ప్రత్యామ్నాయం కాదు. " +
                               "మీ ఆరోగ్య సమస్యలకు అర్హత కలిగిన డాక్టర్‌ని సంప్రదించండి.",
                        modifier  = Modifier.padding(16.dp),
                        fontSize  = 13.sp,
                        color     = Color(0xFF795548),
                        lineHeight = 20.sp
                    )
                }
            }

            item {
                Text(
                    "డాక్టర్ లాస్య v1.0.0 — మీ ఆరోగ్యమే నా బాధ్యత 🌸",
                    fontSize  = 12.sp,
                    color     = LaasyaColors.TextSecondary,
                    modifier  = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text       = title,
        fontWeight = FontWeight.Bold,
        fontSize   = 16.sp,
        color      = LaasyaColors.Pink60,
        modifier   = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun SettingsToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(icon, null, tint = LaasyaColors.Pink60, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text(subtitle, fontSize = 12.sp, color = LaasyaColors.TextSecondary)
                }
            }
            Switch(
                checked   = checked,
                onCheckedChange = onToggle,
                colors    = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LaasyaColors.Pink60)
            )
        }
    }
}
