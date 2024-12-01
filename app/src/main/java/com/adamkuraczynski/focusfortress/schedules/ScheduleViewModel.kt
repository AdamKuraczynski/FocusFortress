package com.adamkuraczynski.focusfortress.schedules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import com.adamkuraczynski.focusfortress.database.Schedule
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ScheduleViewModel : ViewModel() {

    private val scheduleDao = FocusFortressApp.database.scheduleDao()

    val allSchedules: StateFlow<List<Schedule>> = scheduleDao.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeSchedule: StateFlow<Schedule?> = scheduleDao.getActiveSchedule()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    
    fun selectSchedule(scheduleId: Int) {
        viewModelScope.launch {
            scheduleDao.deactivateAllSchedules()
            scheduleDao.activateSchedule(scheduleId)
        }
    }

    
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


private fun getSchedules(): List<Schedule> {
    return listOf(
        Schedule(
            name = "Always On",
            daysOfWeek = "1,2,3,4,5,6,7", // All days
            startTime = "00:00",
            endTime = "23:59",
            isActive = true
        ),
        Schedule(
            name = "Weekdays",
            daysOfWeek = "2,3,4,5,6", // Monday to Friday
            startTime = "00:00",
            endTime = "23:59"
        ),
        Schedule(
            name = "Weekends",
            daysOfWeek = "1,7", // Sunday and Saturday
            startTime = "00:00",
            endTime = "23:59"
        ),
        Schedule(
            name = "Work Hours",
            daysOfWeek = "2,3,4,5,6", // Monday to Friday
            startTime = "09:00",
            endTime = "17:00"
        ),
        Schedule(
            name = "Evenings",
            daysOfWeek = "1,2,3,4,5,6,7", // All days
            startTime = "18:00",
            endTime = "23:00"
        )
    )
}