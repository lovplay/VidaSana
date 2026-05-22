package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SleepEntity
import com.example.data.model.StepsEntity
import com.example.data.model.HydrationEntity
import com.example.ui.theme.TextLowContrast

@Composable
fun StepsLineChart(
    stepsLogs: List<StepsEntity>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Pasos Realizados (Tendencia)",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (stepsLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay suficientes datos de pasos.", color = TextLowContrast, fontSize = 14.sp)
            }
        } else {
            // Sort chronic chronologically
            val sortedLogs = stepsLogs.take(7).reversed()
            val maxSteps = (sortedLogs.maxOfOrNull { it.count } ?: 10000).coerceAtLeast(5000).toFloat()

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val width = size.width
                val height = size.height
                val paddingX = 40f
                val paddingY = 20f

                val chartWidth = width - (paddingX * 2)
                val chartHeight = height - (paddingY * 2)

                val points = sortedLogs.mapIndexed { idx, entity ->
                    val x = paddingX + (idx * (chartWidth / (sortedLogs.size - 1).coerceAtLeast(1)))
                    val normalizedValue = entity.count.toFloat() / maxSteps
                    val y = height - paddingY - (normalizedValue * chartHeight)
                    Offset(x, y)
                }

                // Draw background grid lines
                for (i in 0..4) {
                    val yLine = paddingY + (i * (chartHeight / 4))
                    drawLine(
                        color = Color(0x1AFFFFFF),
                        start = Offset(paddingX, yLine),
                        end = Offset(width - paddingX, yLine),
                        strokeWidth = 2f
                    )
                }

                // Draw gradient fill under the line
                if (points.size > 1) {
                    val path = Path().apply {
                        moveTo(points.first().x, height - paddingY)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, height - paddingY)
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )
                }

                // Draw line
                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = primaryColor,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                }

                // Draw points and labels
                points.forEachIndexed { i, point ->
                    drawCircle(
                        color = secondaryColor,
                        radius = 8f,
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = point
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Row of labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sortedLogs.forEach { log ->
                    val dateLabel = log.date.substringAfterLast("-")
                    Text(
                        text = dateLabel,
                        color = TextLowContrast,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SleepBarChart(
    sleepLogs: List<SleepEntity>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val barColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Horas de Sueño Mensual / Semanal",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (sleepLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay suficientes datos de sueño.", color = TextLowContrast, fontSize = 14.sp)
            }
        } else {
            val sortedLogs = sleepLogs.take(7).reversed()
            val maxHours = 10f // Cap max height at 10 hours for scaling

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                sortedLogs.forEach { log ->
                    val percentage = (log.hours / maxHours).coerceAtMost(1.0).toFloat()
                    val hasMetGoal = log.hours >= 7.0

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = String.format("%.1f", log.hours),
                            color = if (hasMetGoal) MintGreen else CoralAlert,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight(percentage)
                                .background(
                                    if (hasMetGoal) barColor else Color(0xFFF43F5E),
                                    RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = log.date.substringAfterLast("-"),
                            color = TextLowContrast,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// Global Custom Colors definitions for Charts
val MintGreen = Color(0xFF34D399)
val CoralAlert = Color(0xFFFB7185)

@Composable
fun HydrationWaveDashboard(
    volumeMl: Int,
    targetMl: Int = 2000,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    val progress = (volumeMl.toFloat() / targetMl).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Nivel de Hidratación",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Circle Track
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0x1F2DD4BF),
                        style = Stroke(width = 12.dp.toPx())
                    )
                }

                // Progress Circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = secondaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$percentage%",
                        color = secondaryColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "$volumeMl / $targetMl ml",
                        color = TextLowContrast,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
