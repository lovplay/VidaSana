package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // e.g. "Trabajo", "Ejercicio", "Salud", "Personal"
    val priority: String, // e.g. "Alta", "Media", "Baja"
    val isCompleted: Boolean = false,
    val date: String, // YYYY-MM-DD
    val reminderTimeMs: Long? = null // Epoch milliseconds for when to remind
)

@Entity(tableName = "sleep_logs")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val hours: Double
)

@Entity(tableName = "routine_logs")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val date: String, // YYYY-MM-DD
    val category: String = "Salud" // e.g. "Mañana", "Tarde", "Noche"
)

@Entity(tableName = "heart_rate_logs")
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bpm: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val label: String // e.g. "Reposo", "Ejercicio", "Sueño"
)

@Entity(tableName = "steps_logs")
data class StepsEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val count: Int
)

@Entity(tableName = "hydration_logs")
data class HydrationEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val volumeMl: Int
)
