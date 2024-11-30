package com.adamkuraczynski.focusfortress.schedules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import com.adamkuraczynski.focusfortress.database.Schedule
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing schedules in the FocusFortress application.
 *
 * This class interacts with the database to retrieve, insert, and update schedules.
 * It provides state flows for observing all schedules and the currently active schedule.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see com.adamkuraczynski.focusfortress.database.ScheduleDao
 * @see kotlinx.coroutines.flow.StateFlow
 */
class ScheduleViewModel : ViewModel() {

    private val scheduleDao = FocusFortressApp.database.scheduleDao()

    val allSchedules: StateFlow<List<Schedule>> = scheduleDao.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeSchedule: StateFlow<Schedule?> = scheduleDao.getActiveSchedule()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    /**
     * Selects a schedule by its ID and activates it, deactivating others.
     *
     * @param scheduleId The ID of the schedule to activate.
     */
    fun selectSchedule(scheduleId: Int) {
        viewModelScope.launch {
            scheduleDao.deactivateAllSchedules()
            scheduleDao.activateSchedule(scheduleId)
        }
    }

    /**
     * Inserts a list of schedules into the database.
     *
     * @param schedules The list of schedules to insert.
     */
    private fun insertSchedules(schedules: List<Schedule>) {
        viewModelScope.launch {
            scheduleDao.deleteAllSchedules()
            for (schedule in schedules) {
                scheduleDao.insertSchedule(schedule)
            }
        }
    }

    init {
        viewModelScope.launch {
            val existingSchedules = scheduleDao.getAllSchedules().stateIn(viewModelScope).value
            if (existingSchedules.isEmpty()) {
                insertSchedules(getSchedules())
            }
        }
    }
}

/**
 * Provides a predefined list of schedules.
 *
 * @return A list of [Schedule] objects.
 */
private fun getSchedules(): List<Schedule> {
    return listOf(
        Schedule(
            name = "Always On",
            daysOfWeek = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY",
            startTime = "00:00",
            endTime = "23:59",
            isActive = true
        ),
        Schedule(
            name = "Weekdays",
            daysOfWeek = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY",
            startTime = "00:00",
            endTime = "23:59"
        ),
        Schedule(
            name = "Weekends",
            daysOfWeek = "SATURDAY,SUNDAY",
            startTime = "00:00",
            endTime = "23:59"
        ),
        Schedule(
            name = "Work Hours",
            daysOfWeek = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY",
            startTime = "09:00",
            endTime = "17:00"
        ),
        Schedule(
            name = "Evenings",
            daysOfWeek = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY",
            startTime = "18:00",
            endTime = "23:00"
        )
    )
}