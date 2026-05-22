package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextLowContrast
import com.example.ui.viewmodel.HealthTaskViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreen(
    viewModel: HealthTaskViewModel,
    modifier: Modifier = Modifier
) {
    val syncing by viewModel.syncing.collectAsState()
    val lastSyncedStr by viewModel.lastSyncedStr.collectAsState()

    var userName by remember { mutableStateOf("Camilo Guzmán") }
    var userEmail by remember { mutableStateOf("camiloguzman.periodista@gmail.com") }

    var selectedDevice by remember { mutableStateOf("Smartwatch Sanus") }
    val devices = listOf("Smartwatch Sanus", "Garmin Venu 3", "Fitbit Sense 2", "Apple Watch")

    var notificationsEnabled by remember { mutableStateOf(true) }
    var waterAlertsEnabled by remember { mutableStateOf(true) }
    var stepsGoalAlertsEnabled by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TITLE ---
        item {
            Column {
                Text(
                    text = "Ajustes y Dispositivos",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Sincroniza y configuras tus preferencias",
                    color = TextLowContrast,
                    fontSize = 13.sp
                )
            }
        }

        // --- ACCOUNT/PROFILE CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color(0x1F34D399))
                            .size(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User info",
                            tint = MintGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userEmail,
                            color = TextLowContrast,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // --- MULTI-DEVICE SYNC CONFIGURATION ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Sincronización Multidispositivo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Conecta tus relojes inteligentes o sensores para importar pasos, pulso y sueño de forma automática y fluida.",
                        fontSize = 12.sp,
                        color = TextLowContrast
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Select hardware device list
                    Text(
                        "Dispositivo Vinculado:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        devices.forEach { dev ->
                            val isSelected = selectedDevice == dev
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0x1F2DD4BF) else Color(0x0FFFFFFF))
                                    .clickable { selectedDevice = dev }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Check,
                                        contentDescription = "Select",
                                        tint = if (isSelected) TealAccent else TextLowContrast,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = dev,
                                        color = if (isSelected) TealAccent else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                if (isSelected) {
                                    Text("Activo", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sync action trigger
                    Button(
                        onClick = { viewModel.triggerSync() },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_sync_button"),
                        enabled = !syncing
                    ) {
                        if (syncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sincronizando...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Forzar Sincronización Ahora", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Última sincronización en la nube: $lastSyncedStr",
                        color = TextLowContrast,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // --- AUTOMATED NOTIFICATIONS & ALERTS CHANNELS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Alertas y Recordatorios Automáticos",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Recordar Tareas Diarias", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Enviar notificaciones para actividades con alarmas.", fontSize = 11.sp, color = TextLowContrast)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MintGreen,
                                checkedTrackColor = Color(0x3F34D399)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Alerta de Hidratación", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Avisar cada 2 horas para beber un vaso de agua.", fontSize = 11.sp, color = TextLowContrast)
                        }
                        Switch(
                            checked = waterAlertsEnabled,
                            onCheckedChange = { waterAlertsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MintGreen,
                                checkedTrackColor = Color(0x3F34D399)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Meta de Pasos Diaria", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Avisar cuando se cumplan los 10,000 pasos.", fontSize = 11.sp, color = TextLowContrast)
                        }
                        Switch(
                            checked = stepsGoalAlertsEnabled,
                            onCheckedChange = { stepsGoalAlertsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MintGreen,
                                checkedTrackColor = Color(0x3F34D399)
                            )
                        )
                    }
                }
            }
        }
    }
}
