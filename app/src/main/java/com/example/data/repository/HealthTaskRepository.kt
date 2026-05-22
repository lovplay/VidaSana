package com.example.data.repository

import com.example.data.dao.HealthTaskDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class HealthTaskRepository(private val dao: HealthTaskDao) {

    // --- TASKS ---
    fun getAllTasks(): Flow<List<TaskEntity>> = dao.getAllTasks()
    fun getTasksForDate(date: String): Flow<List<TaskEntity>> = dao.getTasksForDate(date)
    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun updateTaskCompletion(id: Int, isCompleted: Boolean) = dao.updateTaskCompletion(id, isCompleted)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)

    // --- SLEEP ---
    fun getSleepLogs(): Flow<List<SleepEntity>> = dao.getSleepLogs()
    suspend fun getSleepForDate(date: String): SleepEntity? = dao.getSleepForDate(date)
    suspend fun insertSleep(sleep: SleepEntity) = dao.insertSleep(sleep)
    suspend fun deleteSleep(sleep: SleepEntity) = dao.deleteSleep(sleep)

    // --- ROUTINES ---
    fun getRoutinesForDate(date: String): Flow<List<RoutineEntity>> = dao.getRoutinesForDate(date)
    suspend fun insertRoutine(routine: RoutineEntity) = dao.insertRoutine(routine)
    suspend fun updateRoutineCompletion(id: Int, isCompleted: Boolean) = dao.updateRoutineCompletion(id, isCompleted)
    suspend fun deleteRoutine(routine: RoutineEntity) = dao.deleteRoutine(routine)

    // --- HEART RATE ---
    fun getHeartRateLogs(): Flow<List<HeartRateEntity>> = dao.getHeartRateLogs()
    suspend fun insertHeartRate(heartRate: HeartRateEntity) = dao.insertHeartRate(heartRate)

    // --- STEPS ---
    fun getStepsForDate(date: String): Flow<StepsEntity?> = dao.getStepsForDate(date)
    fun getStepsLogs(): Flow<List<StepsEntity>> = dao.getStepsLogs()
    suspend fun insertSteps(steps: StepsEntity) = dao.insertSteps(steps)

    // --- HYDRATION ---
    fun getHydrationForDate(date: String): Flow<HydrationEntity?> = dao.getHydrationForDate(date)
    fun getHydrationLogs(): Flow<List<HydrationEntity>> = dao.getHydrationLogs()
    suspend fun insertHydration(hydration: HydrationEntity) = dao.insertHydration(hydration)
}
