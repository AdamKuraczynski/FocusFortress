package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing database operations on schedules.
 *
 * This interface defines methods for inserting, updating, querying, and deleting schedules
 * in the database.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.room.Dao
 * @see kotlinx.coroutines.flow.Flow
 */
@Dao
interface ScheduleDao {

    /**
     * Inserts a [Schedule] into the database.
     *
     * @param schedule The [Schedule] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    /**
     * Updates an existing [Schedule] in the database.
     *
     * @param schedule The [Schedule] to update.
     */
    @Update
    suspend fun updateSchedule(schedule: Schedule)

    /**
     * Retrieves all schedules from the database.
     *
     * @return A [Flow] emitting a list of [Schedule] objects.
     */
    @Query("SELECT * FROM schedules")
    fun getAllSchedules(): Flow<List<Schedule>>

    /**
     * Retrieves the currently active schedule.
     *
     * @return A [Flow] emitting the active [Schedule], or `null` if none are active.
     */
    @Query("SELECT * FROM schedules WHERE isActive = 1 LIMIT 1")
    fun getActiveSchedule(): Flow<Schedule?>

    /**
     * Deactivates all schedules in the database.
     */
    @Query("UPDATE schedules SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAllSchedules()

    /**
     * Activates a schedule by its ID.
     *
     * @param scheduleId The ID of the schedule to activate.
     */
    @Query("UPDATE schedules SET isActive = 1 WHERE id = :scheduleId")
    suspend fun activateSchedule(scheduleId: Int)

    /**
     * Retrieves a schedule by its ID.
     *
     * @param scheduleId The ID of the schedule to retrieve.
     * @return The [Schedule] object, or `null` if not found.
     */
    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Int): Schedule?

    /**
     * Deletes all schedules from the database.
     */
    @Query("DELETE FROM schedules")
    suspend fun deleteAllSchedules()
}