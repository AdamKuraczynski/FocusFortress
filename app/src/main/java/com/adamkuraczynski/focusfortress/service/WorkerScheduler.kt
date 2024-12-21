package com.adamkuraczynski.focusfortress.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WorkerScheduler(private val context: Context) {

    companion object {
        private const val UNIQUE_WORK_NAME = "DailyMotivationWork"
    }

    fun scheduleDailyMotivationWorker() {
        val initialDelay = calculateInitialDelay()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<MotivationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(getConstraints())
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis - System.currentTimeMillis()
    }


    //testing function for instant motivation
//    private fun calculateInitialDelay(): Long {
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
//            set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE) + 1)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }
//        return calendar.timeInMillis - System.currentTimeMillis()
//    }


    private fun getConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()
    }
}