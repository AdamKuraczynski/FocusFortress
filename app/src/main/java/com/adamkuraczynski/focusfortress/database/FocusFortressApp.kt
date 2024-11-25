package com.adamkuraczynski.focusfortress.database

import android.app.Application
import androidx.room.Room

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
    }
}