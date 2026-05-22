package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.RoutineEntity
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextLowContrast
import com.example.ui.viewmodel.HealthTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthLogsScreen(
    viewModel: HealthTaskViewModel,
    modifier: Modifier = Modifier
) {
    val currentDate by viewModel.currentDate.collectAsState()
    val stepsObj by viewModel.stepsToday.collectAsState()
    val hydrationObj by viewModel.hydrationToday.collectAsState()
    val routines by viewModel.routinesForToday.collectAsState()
    val heartRates by viewModel.heartRateLogs.collectAsState()

    var sleepInputHours by remember { mutableStateOf("7.5") }
    var pulseInputBpm by remember { mutableStateOf("70") }
    var pulseLabel by remember { mutableStateOf("Reposo") } // "Reposo", "Ejercicio", "Sueño"

    var newRoutineTitle by remember { mutableStateOf("") }
    var newRoutineCat by remember { mutableStateOf("Mañana") }

    val stepsCount = stepsObj?.count ?: 0
    val waterVolume = hydrationObj?.volumeMl ?: 0

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
                    text = "Registro Saludable",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Controla tus métricas en tiempo real",
                    color = TextLowContrast,
                    fontSize = 13.sp
                )
            }
        }

        // --- GRID LOGS BAR: PASOS & AGUA ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Steps Log card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Pasos Hoy", color = TextLowContrast, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$stepsCount",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MintGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.addSteps(1000) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F34D399)),
                                modifier = Modifier
                                    .testTag("log_steps_add_btn")
                                    .height(32.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("+1K", color = MintGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { viewModel.addSteps(-1000) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x0FFFFFFF)),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("-1K", color = TextLowContrast, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Water Log card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Hidratación", color = TextLowContrast, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$waterVolume ml",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TealAccent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.addWater(250) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F2DD4BF)),
                                modifier = Modifier
                                    .testTag("log_water_add_btn")
                                    .height(32.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text("+250ml", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { viewModel.addWater(-250) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x0FFFFFFF)),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("-250", color = TextLowContrast, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- HOURS OF SLEEP LOGGER ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Horas de Sueño Anoche",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = sleepInputHours,
                            onValueChange = { sleepInputHours = it },
                            label = { Text("Ej: 7.5 u 8") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sleep_hours_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreen,
                                unfocusedBorderColor = TextLowContrast
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val h = sleepInputHours.toDoubleOrNull() ?: 8.0
                                viewModel.logSleep(h)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .testTag("log_sleep_btn")
                                .height(56.dp)
                        ) {
                            Text("Logguear", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- HEART RATE BPM LOGGER ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Registro de Ritmo Cardíaco (Pulso)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pulseInputBpm,
                            onValueChange = { pulseInputBpm = it },
                            label = { Text("BPM (p. ej. 68)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("heart_rate_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CoralAlert,
                                unfocusedBorderColor = TextLowContrast
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Trigger pulse save
                        Button(
                            onClick = {
                                val bpmVal = pulseInputBpm.toIntOrNull() ?: 70
                                viewModel.logHeartRate(bpmVal, pulseLabel)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .testTag("log_pulse_btn")
                                .height(56.dp)
                        ) {
                            Text("Grabar", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // State labels (Reposo, Ejercicio etc)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val labels = listOf("Reposo", "Ejercicio", "Sueño")
                        labels.forEach { l ->
                            val isSelected = pulseLabel == l
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) CoralAlert else Color(0x1F2DD4BF))
                                    .clickable { pulseLabel = l }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = l,
                                    color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- MANAGE DAILY ROUTINES & HEALTH GOALS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Crear Rutinas Diarias Personalizadas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newRoutineTitle,
                        onValueChange = { newRoutineTitle = it },
                        label = { Text("Ej: Meditación guiada, Stretching") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("routine_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            unfocusedBorderColor = TextLowContrast
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Category (Morning/Afternoon/Night)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Mañana", "Tarde", "Noche").forEach { cat ->
                                val isSelected = newRoutineCat == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) TealAccent else Color(0x0FFFFFFF))
                                        .clickable { newRoutineCat = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (newRoutineTitle.isNotBlank()) {
                                    viewModel.addCustomRoutine(newRoutineTitle, newRoutineCat)
                                    newRoutineTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .testTag("add_custom_routine_btn")
                                .height(36.dp)
                        ) {
                            Text("Añadir", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- HISTORICAL PULSE LIST ---
        item {
            Text(
                "Historial de Pulso (BPM)",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (heartRates.isEmpty()) {
            item {
                Text("No hay lecturas registradas.", color = TextLowContrast, fontSize = 12.sp)
            }
        } else {
            items(heartRates.take(5)) { hr ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Pulse",
                            tint = CoralAlert,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${hr.bpm} BPM",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x1FFB7185))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = hr.label,
                            color = CoralAlert,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
