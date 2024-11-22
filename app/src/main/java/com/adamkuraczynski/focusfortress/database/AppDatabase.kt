package com.adamkuraczynski.focusfortress.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BlockedApp::class, BlockedWebsite::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun blockedWebsiteDao(): BlockedWebsiteDao
}