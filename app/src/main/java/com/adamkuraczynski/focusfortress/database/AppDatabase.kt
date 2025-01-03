package com.adamkuraczynski.focusfortress.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [
        BlockedApp::class,
        BlockedWebsite::class,
        BlockedKeyword::class,
        Passcode::class,
        Schedule::class,
        Achievement::class
    ],
    version = 8
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun blockedAppDao(): BlockedAppDao

    
    abstract fun blockedWebsiteDao(): BlockedWebsiteDao

    
    abstract fun blockedKeywordDao(): BlockedKeywordDao

    
    abstract fun passcodeDao(): PasscodeDao

    
    abstract fun scheduleDao(): ScheduleDao


    abstract fun achievementDao(): AchievementDao
}