package com.adamkuraczynski.focusfortress.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BlockedApp::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
}