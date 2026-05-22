package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextLowContrast
import com.example.ui.viewmodel.HealthTaskViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TasksScreen(
    viewModel: HealthTaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()

    var isAddingTask by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var taskCategory by remember { mutableStateOf("Trabajo") }
    var taskPriority by remember { mutableStateOf("Media") }
    var taskReminder by remember { mutableStateOf("Sin recordatorio") }

    // Categories we support
    val categories = listOf("Trabajo", "Salud", "Routines", "Personal", "Deporte")
    // Priorities we support
    val priorities = listOf("Alta", "Media", "Baja")
    // Filters
    var currentFilter by remember { mutableStateOf("Pendientes") } // "Todas", "Pendientes", "Completadas"
    var searchKeyword by remember { mutableStateOf("") }

    val filteredTasks = tasks.filter { task ->
        val matchesFilter = when (currentFilter) {
            "Pendientes" -> !task.isCompleted
            "Completadas" -> task.isCompleted
            else -> true
        }
        val matchesSearch = task.title.contains(searchKeyword, ignoreCase = true) ||
                task.description.contains(searchKeyword, ignoreCase = true) ||
                task.category.contains(searchKeyword, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- TITLE & ADD TASK TOGGLE ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Gestión de Tareas",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Organiza tu día eficientemente",
                    color = TextLowContrast,
                    fontSize = 13.sp
                )
            }

            FloatingActionButton(
                onClick = { isAddingTask = !isAddingTask },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .testTag("add_task_fab")
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = if (isAddingTask) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Agregar Tarea"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- EXPANDABLE CARD TO ADD TASK ---
        AnimatedVisibility(
            visible = isAddingTask,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Nueva Actividad",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("¿Qué tienes que hacer?") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            unfocusedBorderColor = TextLowContrast,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    OutlinedTextField(
                        value = taskDesc,
                        onValueChange = { taskDesc = it },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            unfocusedBorderColor = TextLowContrast,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Category Selector Row
                    Text("Categoría:", color = TextLowContrast, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            val isSelected = taskCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TealAccent else Color(0x1F94A3B8))
                                    .clickable { taskCategory = cat }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Priority Row
                    Text("Prioridad:", color = TextLowContrast, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        priorities.forEach { prio ->
                            val isSelected = taskPriority == prio
                            val color = when (prio) {
                                "Alta" -> CoralAlert
                                "Media" -> TealAccent
                                else -> TextLowContrast
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) color else Color(0x0F94A3B8))
                                    .clickable { taskPriority = prio }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prio,
                                    color = if (isSelected) Color(0xFF0F172A) else color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Simulated Alarm/Reminder Field
                    Text("Configurar Recordatorio Automático:", color = TextLowContrast, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val intervals = listOf("Cada mañana (08:00)", "Media tarde (16:00)", "Cada noche (21:00)")
                        intervals.forEach { interval ->
                            val isSelected = taskReminder == interval
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MintGreen else Color(0x1F94A3B8))
                                    .clickable {
                                        taskReminder = if (isSelected) "Sin recordatorio" else interval
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = interval.substringAfter("(").substringBefore(")"),
                                    color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (taskTitle.isNotBlank()) {
                                viewModel.addTask(
                                    title = taskTitle,
                                    description = if (taskReminder != "Sin recordatorio") "$taskDesc (Recordatorio a las ${taskReminder.substringAfter("(").substringBefore(")")})" else taskDesc,
                                    category = taskCategory,
                                    priority = taskPriority
                                )
                                // Reset fields
                                taskTitle = ""
                                taskDesc = ""
                                taskCategory = "Trabajo"
                                taskPriority = "Media"
                                taskReminder = "Sin recordatorio"
                                isAddingTask = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_task_button")
                    ) {
                        Text("Crear Tarea", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- FILTER CHIPS & SEARCH BAR ---
        OutlinedTextField(
            value = searchKeyword,
            onValueChange = { searchKeyword = it },
            placeholder = { Text("Buscar tareas...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "S", tint = TextLowContrast) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MintGreen,
                unfocusedBorderColor = TextLowContrast
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val listFilters = listOf("Todas", "Pendientes", "Completadas")
            listFilters.forEach { filter ->
                val isSelected = currentFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .clickable { currentFilter = filter }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TASKS LIST CONTAINER ---
        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No hay tareas",
                        tint = TextLowContrast,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sin tareas en esta vista",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Intenta buscar otra palabra o agrega una nueva tarea.",
                        color = TextLowContrast,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_item_${task.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Bullet Priority indicator
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (task.priority) {
                                                    "Alta" -> CoralAlert
                                                    "Media" -> TealAccent
                                                    else -> TextLowContrast
                                                }
                                            )
                                            .size(8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (task.isCompleted) TextLowContrast else MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                        maxLines = 1
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (task.description.isEmpty()) "Sin descripción" else task.description,
                                    color = TextLowContrast,
                                    fontSize = 13.sp,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0x0FFFFFFF))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = task.category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TealAccent
                                        )
                                    }
                                    if (task.description.contains("Recordatorio")) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Remind",
                                            tint = MintGreen,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text("Alerta activa", color = MintGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleTask(task) },
                                    colors = CheckboxDefaults.colors(checkedColor = MintGreen)
                                )

                                IconButton(
                                    onClick = { viewModel.deleteTask(task) },
                                    modifier = Modifier.testTag("delete_task_button_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Borrar",
                                        tint = CoralAlert.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
