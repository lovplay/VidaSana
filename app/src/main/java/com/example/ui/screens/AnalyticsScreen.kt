package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SleepBarChart
import com.example.ui.components.StepsLineChart
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextLowContrast
import com.example.ui.viewmodel.HealthTaskViewModel

@Composable
fun AnalyticsScreen(
    viewModel: HealthTaskViewModel,
    modifier: Modifier = Modifier
) {
    val stepsLogs by viewModel.stepsLogs.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val hydrationLogs by viewModel.hydrationLogs.collectAsState()

    val averageSteps = if (stepsLogs.isNotEmpty()) stepsLogs.map { it.count }.average().toInt() else 0
    val averageSleep = if (sleepLogs.isNotEmpty()) sleepLogs.map { it.hours }.average() else 0.0
    val averageHydration = if (hydrationLogs.isNotEmpty()) hydrationLogs.map { it.volumeMl }.average().toInt() else 0

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
                    text = "Análisis Mensual",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Progreso gráfico de tu bienestar integral",
                    color = TextLowContrast,
                    fontSize = 13.sp
                )
            }
        }

        // --- CHART 1: STEPS TREND COMLINED CANVAS ---
        item {
            StepsLineChart(
                stepsLogs = stepsLogs,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("steps_line_chart")
            )
        }

        // --- CHART 2: SLEEP BARS COMPOSE CANVAS ---
        item {
            SleepBarChart(
                sleepLogs = sleepLogs,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sleep_bar_chart")
            )
        }

        // --- STATS OVERVIEW SUMMARY CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Promedios del Período",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Paso Diario", color = TextLowContrast, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$averageSteps", color = MintGreen, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Sueño Nocturno", color = TextLowContrast, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(String.format("%.1f h", averageSleep), color = TealAccent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Sorbos Agua", color = TextLowContrast, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${averageHydration}ml", color = TealAccent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // --- MOTIVATIONAL HEALTH TIPS CODES ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x0C2DD4BF))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Tips",
                        tint = TealAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Consejo de VidaSana",
                            color = TealAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Un buen ritmo de hidratación de 2L al día y dormir 7.5h incrementa la productividad mental en un 30%. ¡Sigue logueando tus metas!",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
