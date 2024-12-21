package com.adamkuraczynski.focusfortress

import android.app.Application
import androidx.room.Room
import com.adamkuraczynski.focusfortress.database.AppDatabase
import com.adamkuraczynski.focusfortress.service.WorkerScheduler


class FocusFortressApp : Application() {

    companion object {
        
        lateinit var database: AppDatabase
            private set
    }

    
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "focus_fortress_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        val workerScheduler = WorkerScheduler(this)
        workerScheduler.scheduleDailyMotivationWorker()
    }
}