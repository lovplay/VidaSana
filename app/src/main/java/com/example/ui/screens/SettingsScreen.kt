package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val syncing by viewModel.syncing.collectAsState()
    val lastSyncedStr by viewModel.lastSyncedStr.collectAsState()

    // Live User details collected from SharedPreferences session
    val currentUserName by viewModel.currentUserName.collectAsState()
    val currentUserEmail by viewModel.currentUserEmail.collectAsState()
    val isGoogleUser by viewModel.isGoogleUser.collectAsState()

    // Permissions statuses from ViewModel
    val notifPermission by viewModel.notifPermission.collectAsState()
    val gpsPermission by viewModel.gpsPermission.collectAsState()
    val motionPermission by viewModel.motionPermission.collectAsState()
    val bootPermission by viewModel.bootPermission.collectAsState()

    var selectedDevice by remember { mutableStateOf("Smartwatch Sanus") }
    val devices = listOf("Smartwatch Sanus", "Garmin Venu 3", "Fitbit Sense 2", "Apple Watch")

    var waterAlertsEnabled by remember { mutableStateOf(true) }
    var stepsGoalAlertsEnabled by remember { mutableStateOf(true) }

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
                    text = "Sincroniza y configuras tus sensores o preferencias",
                    color = TextLowContrast,
                    fontSize = 13.sp
                )
            }
        }

        // --- ACCOUNT/PROFILE CARD WITH LOGOUT ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(if (isGoogleUser) Color(0x1F4285F4) else Color(0x1F8B5CF6))
                                .size(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isGoogleUser) Icons.Default.AccountBox else Icons.Default.Person,
                                contentDescription = "Perfil u cuenta",
                                tint = if (isGoogleUser) Color(0xFF4285F4) else MintGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUserName.ifBlank { "Usuario de Pulsefy" },
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isGoogleUser) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF4285F4).copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("Google", color = Color(0xFF4285F4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = currentUserEmail.ifBlank { "invitado@pulsefy.com" },
                                color = TextLowContrast,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Logout trigger
                    IconButton(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = CoralAlert)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            }
        }

        // --- INTERACTIVE SYSTEM PERMISSIONS & SENSORS CONFIGURATION ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sensores y Permisos del Sistema",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Configura los captadores de hardware de tu teléfono para permitir el análisis continuo de métricas.",
                        fontSize = 11.sp,
                        color = TextLowContrast
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. GPS / Location
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "GPS",
                                tint = if (gpsPermission) TealAccent else TextLowContrast,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Localización GPS", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Necesario para mapear rutas de ejercicio y altitud.", fontSize = 10.sp, color = TextLowContrast)
                            }
                        }
                        Switch(
                            checked = gpsPermission,
                            onCheckedChange = { granted ->
                                viewModel.setPermissionState("gps", granted)
                                val msg = if (granted) "Permiso de GPS (Fine/Coarse location) concedido" else "Acceso a GPS inhabilitado"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TealAccent,
                                checkedTrackColor = TealAccent.copy(alpha = 0.3f)
                            )
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    // 2. Motion Sensors
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Pasos",
                                tint = if (motionPermission) TealAccent else TextLowContrast,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Sensores de Movimiento", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Permite contar pasos y calorías usando el acelerómetro virtual.", fontSize = 10.sp, color = TextLowContrast)
                            }
                        }
                        Switch(
                            checked = motionPermission,
                            onCheckedChange = { granted ->
                                viewModel.setPermissionState("motion", granted)
                                val msg = if (granted) "Permiso de Sensores de Movimiento activos" else "Podómetro desactivado"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TealAccent,
                                checkedTrackColor = TealAccent.copy(alpha = 0.3f)
                            )
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    // 3. Notifications
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alertas",
                                tint = if (notifPermission) TealAccent else TextLowContrast,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Notificaciones Push", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Envío diario de alertas de hidratación y tareas.", fontSize = 10.sp, color = TextLowContrast)
                            }
                        }
                        Switch(
                            checked = notifPermission,
                            onCheckedChange = { granted ->
                                viewModel.setPermissionState("notification", granted)
                                val msg = if (granted) "Permiso de mensajes y notificaciones concedido" else "Notificaciones apagadas"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TealAccent,
                                checkedTrackColor = TealAccent.copy(alpha = 0.3f)
                            )
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    // 4. Power Startup / Boot Completed
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Inicio",
                                tint = if (bootPermission) TealAccent else TextLowContrast,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Inicio Inteligente (Encendido)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Configura la activación en segundo plano tras encender o reiniciar.", fontSize = 10.sp, color = TextLowContrast)
                            }
                        }
                        Switch(
                            checked = bootPermission,
                            onCheckedChange = { granted ->
                                viewModel.setPermissionState("boot", granted)
                                val msg = if (granted) "Servicio configurado para iniciar en encendido automático" else "Inicio automático desactivado"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TealAccent,
                                checkedTrackColor = TealAccent.copy(alpha = 0.3f)
                            )
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

        // --- OTHER UTILITIES ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Alertas Secundarias",
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
                            Text("Alerta de Hidratación Alterna", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Zumbido menor cada hora.", fontSize = 11.sp, color = TextLowContrast)
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
                            Text("Meta de Pasos Secundaria", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Notificar a mitad de meta.", fontSize = 11.sp, color = TextLowContrast)
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
