package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a schedule for blocking functionality.
 *
 * This entity is stored in the "schedules" table in the Room database.
 *
 * @property id The unique identifier for the schedule.
 * @property name The name of the schedule.
 * @property daysOfWeek A comma-separated string of day numbers when the schedule is active
 * @property startTime The start time of the schedule in "HH:mm" format.
 * @property endTime The end time of the schedule in "HH:mm" format.
 * @property isActive Indicates whether the schedule is currently active.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.2
 *
 * @see androidx.room.Entity
 * @see androidx.room.PrimaryKey
 */
@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val daysOfWeek: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean = false
)