package com.adamkuraczynski.focusfortress.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The main database class for the FocusFortress application.
 *
 * This class defines the database configuration and serves as the main access point
 * to the persisted data using Room.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.6
 *
 * @see androidx.room.Database
 * @see androidx.room.RoomDatabase
 */
@Database(
    entities = [
        BlockedApp::class,
        BlockedWebsite::class,
        BlockedKeyword::class,
        Passcode::class,
        Schedule::class
    ],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides access to the [BlockedAppDao] for blocked apps operations.
     *
     * @return An instance of [BlockedAppDao].
     */
    abstract fun blockedAppDao(): BlockedAppDao

    /**
     * Provides access to the [BlockedWebsiteDao] for blocked websites operations.
     *
     * @return An instance of [BlockedWebsiteDao].
     */
    abstract fun blockedWebsiteDao(): BlockedWebsiteDao

    /**
     * Provides access to the [BlockedKeywordDao] for blocked keywords operations.
     *
     * @return An instance of [BlockedKeywordDao].
     */
    abstract fun blockedKeywordDao(): BlockedKeywordDao

    /**
     * Provides access to the [PasscodeDao] for passcode operations.
     *
     * @return An instance of [PasscodeDao].
     */
    abstract fun passcodeDao(): PasscodeDao

    /**
     * Provides access to the [ScheduleDao] for schedule operations.
     *
     * @return An instance of [ScheduleDao].
     */
    abstract fun scheduleDao(): ScheduleDao
}