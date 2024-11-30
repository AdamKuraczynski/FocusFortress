package com.adamkuraczynski.focusfortress.database

import android.app.Application
import androidx.room.Room

/**
 * Custom Application class for initializing the Room database.
 *
 * This class initializes the database when the application starts
 * and provides a static reference to it.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.3
 *
 * @see android.app.Application
 * @see androidx.room.Room
 */
class FocusFortressApp : Application() {

    companion object {
        /**
         * The singleton instance of [AppDatabase].
         *
         * This property provides global access to the database throughout the app.
         */
        lateinit var database: AppDatabase
            private set
    }

    /**
     * Called when the application is starting.
     *
     * Initializes the Room database with a fallback strategy for destructive migrations.
     */
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