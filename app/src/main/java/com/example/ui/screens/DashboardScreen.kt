package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthTaskViewModel,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDate by viewModel.currentDate.collectAsState()
    val tasks by viewModel.tasksForToday.collectAsState()
    val routines by viewModel.routinesForToday.collectAsState()
    val stepsObj by viewModel.stepsToday.collectAsState()
    val hydrationObj by viewModel.hydrationToday.collectAsState()
    val heartRateLogs by viewModel.heartRateLogs.collectAsState()
    val isSyncing by viewModel.syncing.collectAsState()

    val pendingTasks = tasks.filter { !it.isCompleted }
    val completedRoutines = routines.filter { it.isCompleted }.size
    val totalRoutines = routines.size

    val stepsCount = stepsObj?.count ?: 0
    val hydrationVolume = hydrationObj?.volumeMl ?: 0
    val lastHeartRate = heartRateLogs.firstOrNull()?.bpm ?: 72

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. BENTO HEADER BLOCK ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currentDate.ifEmpty { "Fecha de Hoy" }.uppercase(),
                    color = BentoHeaderPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Hola, VidaSana",
                    color = BentoSleepDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Device Sync Button
                IconButton(
                    onClick = { viewModel.triggerSync() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoNavBg)
                        .testTag("dashboard_sync_button")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = BentoHeaderPurple,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = BentoHeaderPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Bento Profile Avatar Badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BentoNavPillBg)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "V",
                        color = BentoHeaderPurple,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // --- 2. BENTO SECTION: STEPS & SLEEP SIDE-BY-SIDE ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Steps Card (Bento 2x2, weight 1.1)
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .height(190.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(BentoPurple)
                    .clickable { onNavigateToTab(2) }
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "PASOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoPurpleText.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format("%,d", stepsCount),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoPurpleText,
                        lineHeight = 32.sp
                    )
                }

                Column {
                    // Quick Increment Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+500 pasos",
                            color = BentoPurpleText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.addSteps(500) },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "+500",
                                tint = BentoPurpleText,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress slider line representation
                    val progressFraction = (stepsCount.toFloat() / 10000f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BentoPurpleText.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .fillMaxHeight()
                                .background(BentoPurpleText)
                        )
                    }
                }
            }

            // Sleep Card (Bento 2x2, weight 0.9)
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .height(190.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(BentoSleepDark)
                    .clickable { onNavigateToTab(3) }
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SUEÑO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "🌙",
                        fontSize = 13.sp
                    )
                }

                Column {
                    Text(
                        text = "7h 45m",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "92% Óptimo",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Styled vertical bar graphics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val barHeights = listOf(0.4f, 0.6f, 0.3f, 0.9f, 0.7f)
                    barHeights.forEach { heightRatio ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightRatio)
                                .background(BentoPurple, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        )
                    }
                }
            }
        }

        // --- 3. BENTO SECTION: PULSE & HYDRATION ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Heart Rate BPM block (Bento 2x1)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BentoPink)
                    .clickable { onNavigateToTab(2) }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "RITMO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoPinkText.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$lastHeartRate",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoPinkText
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "BPM",
                            fontSize = 10.sp,
                            color = BentoPinkText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("♥", color = Color(0xFF962D4C), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Hydration Liters block (Bento 2x1)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BentoBlue)
                    .clickable { onNavigateToTab(2) }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "AGUA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoBlueText.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        val liters = hydrationVolume / 1000f
                        Text(
                            text = String.format("%.1f", liters),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoBlueText
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "L",
                            fontSize = 10.sp,
                            color = BentoBlueText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.addWater(250) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Text("💧", fontSize = 14.sp)
                }
            }
        }

        // --- 4. BENTO SECTION: ROUTINES (Bento 4x2 Lavender block) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(BentoLavender)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Rutinas Diarias",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoLavenderText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BentoLavenderText)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$completedRoutines/$totalRoutines",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Editar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoLavenderText,
                    modifier = Modifier.clickable { onNavigateToTab(2) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (routines.isEmpty()) {
                Text(
                    "No hay rutinas programadas hoy.",
                    color = BentoLavenderText.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    routines.take(3).forEach { routine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.45f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (routine.isCompleted) Icons.Default.CheckCircle else Icons.Default.Star,
                                    contentDescription = "Estado rutina",
                                    tint = if (routine.isCompleted) BentoHeaderPurple else BentoLavenderText.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = routine.title,
                                    color = BentoLavenderText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Checkbox(
                                checked = routine.isCompleted,
                                onCheckedChange = { viewModel.toggleRoutine(routine) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BentoHeaderPurple,
                                    uncheckedColor = BentoLavenderText.copy(alpha = 0.3f),
                                    checkmarkColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- 5. BENTO SECTION: PROGRESS GAUGE & PRIORITY TASKS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progreso circular bento block (Left, weight 1f)
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(28.dp))
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PROGRESO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF49454F),
                    letterSpacing = 1.sp
                )

                // SVG-like Arc Circle Indicator
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFFF3EDF7),
                            style = Stroke(width = 5.dp.toPx())
                        )
                    }

                    // Task done percentage calculate
                    val progressRatio = if (tasks.isNotEmpty()) {
                        (tasks.filter { it.isCompleted }.size.toFloat() / tasks.size.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0.75f // attractive baseline fallback
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = BentoHeaderPurple,
                            startAngle = -90f,
                            sweepAngle = 360f * progressRatio,
                            useCenter = false,
                            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Text(
                        text = String.format("+%d%%", (progressRatio * 100).toInt()),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoSleepDark
                    )
                }

                Text(
                    text = "Mejor que el mes pasado",
                    color = Color(0xFF49454F),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // High Priority Tasks Card (Right, weight 1f)
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(BentoLavender)
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TAREAS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoLavenderText.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BentoHeaderPurple)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${pendingTasks.size}",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (pendingTasks.isEmpty()) {
                    Text(
                        "¡Al día! Todo hecho.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoLavenderText
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        pendingTasks.take(2).forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.5f))
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (task.priority) {
                                                "Alta" -> CoralAlert
                                                "Media" -> TealAccent
                                                else -> TextLowContrast
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoLavenderText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Ver detalles ➜",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoHeaderPurple,
                    modifier = Modifier.clickable { onNavigateToTab(1) }
                )
            }
        }
    }
}
