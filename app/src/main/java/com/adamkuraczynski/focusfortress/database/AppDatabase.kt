package com.adamkuraczynski.focusfortress.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        BlockedApp::class,
        BlockedWebsite::class,
        BlockedKeyword::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun blockedWebsiteDao(): BlockedWebsiteDao
    abstract fun blockedKeywordDao(): BlockedKeywordDao
}