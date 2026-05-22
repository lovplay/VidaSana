package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.HealthTaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HealthTaskViewModel(
    application: Application,
    private val repository: HealthTaskRepository
) : AndroidViewModel(application) {

    // Current selected date for displays (defaults to today)
    private val _currentDate = MutableStateFlow(getTodayDateString())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // --- RECTIVE FLOWS FROM DB ---
    val allTasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasksForToday: StateFlow<List<TaskEntity>> = _currentDate.flatMapLatest { date ->
        repository.getTasksForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routinesForToday: StateFlow<List<RoutineEntity>> = _currentDate.flatMapLatest { date ->
        repository.getRoutinesForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stepsToday: StateFlow<StepsEntity?> = _currentDate.flatMapLatest { date ->
        repository.getStepsForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val hydrationToday: StateFlow<HydrationEntity?> = _currentDate.flatMapLatest { date ->
        repository.getHydrationForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sleepLogs: StateFlow<List<SleepEntity>> = repository.getSleepLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val heartRateLogs: StateFlow<List<HeartRateEntity>> = repository.getHeartRateLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All-time aggregates for analysis
    val stepsLogs: StateFlow<List<StepsEntity>> = repository.getStepsLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hydrationLogs: StateFlow<List<HydrationEntity>> = repository.getHydrationLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- SYNC STATE ---
    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    private val _lastSyncedStr = MutableStateFlow("Nunca")
    val lastSyncedStr: StateFlow<String> = _lastSyncedStr.asStateFlow()

    init {
        // Auto-initialize current day metrics (steps, hydration, routines)
        initializeTodayHealthMetrics()
    }

    fun selectDate(dateString: String) {
        _currentDate.value = dateString
        initializeTodayHealthMetrics()
    }

    private fun initializeTodayHealthMetrics() {
        val date = _currentDate.value
        viewModelScope.launch {
            // Steps
            repository.getStepsForDate(date).first().let {
                if (it == null) {
                    repository.insertSteps(StepsEntity(date = date, count = 2500)) // give a starter base
                }
            }

            // Hydration
            repository.getHydrationForDate(date).first().let {
                if (it == null) {
                    repository.insertHydration(HydrationEntity(date = date, volumeMl = 500)) // starter base
                }
            }

            // Routines (Pre-populate with 5 default daily routines if empty)
            repository.getRoutinesForDate(date).first().let { currentRoutines ->
                if (currentRoutines.isEmpty()) {
                    val defaults = listOf(
                        RoutineEntity(title = "Estiramientos Matutinos", date = date, category = "Mañana"),
                        RoutineEntity(title = "Beber 1 vaso de agua", date = date, category = "Mañana"),
                        RoutineEntity(title = "Meditar 10 minutos", date = date, category = "Tarde"),
                        RoutineEntity(title = "Caminar 5K Pasos", date = date, category = "Tarde"),
                        RoutineEntity(title = "Lectura o desconexión digital", date = date, category = "Noche")
                    )
                    defaults.forEach { routine ->
                        repository.insertRoutine(routine)
                    }
                }
            }

            // Pre-seed some sleep history & pulse history if completely empty
            repository.getSleepLogs().first().let { logs ->
                if (logs.isEmpty()) {
                    seedHistoricalData()
                }
            }
        }
    }

    private suspend fun seedHistoricalData() {
        // Seed some sleep logs for the last 7 days
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val baseHours = listOf(7.2, 8.0, 6.5, 7.5, 8.2, 7.0, 7.8)
        val baseSteps = listOf(8420, 10230, 6100, 9400, 11500, 7800, 8900)
        val baseHydration = listOf(1800, 2200, 1500, 2000, 2500, 1700, 1900)

        for (i in 1..7) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val dateStr = sdf.format(cal.time)
            repository.insertSleep(SleepEntity(date = dateStr, hours = baseHours[i - 1]))
            repository.insertSteps(StepsEntity(date = dateStr, count = baseSteps[i - 1]))
            repository.insertHydration(HydrationEntity(date = dateStr, volumeMl = baseHydration[i - 1]))
        }

        // Return calendar to today
        cal.time = Date()

        // Seed some heart rates
        repository.insertHeartRate(HeartRateEntity(bpm = 64, label = "Reposo"))
        repository.insertHeartRate(HeartRateEntity(bpm = 112, label = "Ejercicio"))
        repository.insertHeartRate(HeartRateEntity(bpm = 72, label = "Reposo"))
        repository.insertHeartRate(HeartRateEntity(bpm = 58, label = "Sueño"))
    }

    // Helper to get today's date as string
    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // --- TASK ACTIONS ---
    fun addTask(title: String, description: String, category: String, priority: String) {
        val date = _currentDate.value
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    date = date
                )
            )
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- HYDRATION ACTIONS ---
    fun addWater(amountMl: Int) {
        val date = _currentDate.value
        viewModelScope.launch {
            val currentVol = repository.getHydrationForDate(date).first()?.volumeMl ?: 0
            repository.insertHydration(HydrationEntity(date = date, volumeMl = (currentVol + amountMl).coerceAtLeast(0)))
        }
    }

    // --- STEPS ACTIONS ---
    fun updateSteps(stepsCount: Int) {
        val date = _currentDate.value
        viewModelScope.launch {
            repository.insertSteps(StepsEntity(date = date, count = stepsCount.coerceAtLeast(0)))
        }
    }

    fun addSteps(amount: Int) {
        val date = _currentDate.value
        viewModelScope.launch {
            val current = repository.getStepsForDate(date).first()?.count ?: 0
            repository.insertSteps(StepsEntity(date = date, count = (current + amount).coerceAtLeast(0)))
        }
    }

    // --- SLEEP ACTIONS ---
    fun logSleep(hours: Double) {
        val date = _currentDate.value
        viewModelScope.launch {
            val existing = repository.getSleepForDate(date)
            if (existing != null) {
                repository.insertSleep(existing.copy(hours = hours))
            } else {
                repository.insertSleep(SleepEntity(date = date, hours = hours))
            }
        }
    }

    // --- HEART RATE ACTIONS ---
    fun logHeartRate(bpm: Int, label: String) {
        viewModelScope.launch {
            repository.insertHeartRate(HeartRateEntity(bpm = bpm, label = label))
        }
    }

    // --- ROUTINE ACTIONS ---
    fun toggleRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            repository.updateRoutineCompletion(routine.id, !routine.isCompleted)
        }
    }

    fun addCustomRoutine(title: String, category: String) {
        val date = _currentDate.value
        viewModelScope.launch {
            repository.insertRoutine(
                RoutineEntity(
                    title = title,
                    isCompleted = false,
                    date = date,
                    category = category
                )
            )
        }
    }

    // --- SIMULATED FLUENT SYNC ---
    fun triggerSync() {
        if (_syncing.value) return
        viewModelScope.launch {
            _syncing.value = true
            // Simulate networking delay & sync processes
            kotlinx.coroutines.delay(1800)
            _syncing.value = false
            val nowTimeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            _lastSyncedStr.value = "Hoy a las $nowTimeStr"
        }
    }
}

class HealthTaskViewModelFactory(
    private val application: Application,
    private val repository: HealthTaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthTaskViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
