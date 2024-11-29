package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedules")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE isActive = 1 LIMIT 1")
    fun getActiveSchedule(): Flow<Schedule?>

    @Query("UPDATE schedules SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAllSchedules()

    @Query("UPDATE schedules SET isActive = 1 WHERE id = :scheduleId")
    suspend fun activateSchedule(scheduleId: Int)

    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Int): Schedule?

    @Query("DELETE FROM schedules")
    suspend fun deleteAllSchedules()
}