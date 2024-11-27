package com.adamkuraczynski.focusfortress.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        BlockedApp::class,
        BlockedWebsite::class,
        BlockedKeyword::class,
        Passcode::class,
    ],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun blockedWebsiteDao(): BlockedWebsiteDao
    abstract fun blockedKeywordDao(): BlockedKeywordDao
    abstract fun passcodeDao(): PasscodeDao
}