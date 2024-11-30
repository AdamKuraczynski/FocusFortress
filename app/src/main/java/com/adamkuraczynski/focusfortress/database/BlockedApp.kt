package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a blocked application.
 *
 * This entity is stored in the "blocked_apps" table in the Room database.
 *
 * @property packageName The unique package name of the application.
 * @property appName The display name of the application.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.room.Entity
 * @see androidx.room.PrimaryKey
 */
@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val appName: String
)