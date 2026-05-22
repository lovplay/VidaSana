package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthTaskDao {

    // --- TASKS ---
    @Query("SELECT * FROM tasks ORDER BY priority ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY id DESC")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: Int, isCompleted: Boolean)

    @Delete
    suspend fun deleteTask(task: TaskEntity)


    // --- SLEEP ---
    @Query("SELECT * FROM sleep_logs ORDER BY date DESC LIMIT 30")
    fun getSleepLogs(): Flow<List<SleepEntity>>

    @Query("SELECT * FROM sleep_logs WHERE date = :date LIMIT 1")
    suspend fun getSleepForDate(date: String): SleepEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepEntity)

    @Delete
    suspend fun deleteSleep(sleep: SleepEntity)


    // --- ROUTINES ---
    @Query("SELECT * FROM routine_logs WHERE date = :date ORDER BY id ASC")
    fun getRoutinesForDate(date: String): Flow<List<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Query("UPDATE routine_logs SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateRoutineCompletion(id: Int, isCompleted: Boolean)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)


    // --- HEART RATE ---
    @Query("SELECT * FROM heart_rate_logs ORDER BY timestamp DESC LIMIT 50")
    fun getHeartRateLogs(): Flow<List<HeartRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeartRate(heartRate: HeartRateEntity)


    // --- STEPS ---
    @Query("SELECT * FROM steps_logs WHERE date = :date LIMIT 1")
    fun getStepsForDate(date: String): Flow<StepsEntity?>

    @Query("SELECT * FROM steps_logs ORDER BY date DESC LIMIT 30")
    fun getStepsLogs(): Flow<List<StepsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: StepsEntity)


    // --- HYDRATION ---
    @Query("SELECT * FROM hydration_logs WHERE date = :date LIMIT 1")
    fun getHydrationForDate(date: String): Flow<HydrationEntity?>

    @Query("SELECT * FROM hydration_logs ORDER BY date DESC LIMIT 30")
    fun getHydrationLogs(): Flow<List<HydrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydration(hydration: HydrationEntity)
}
