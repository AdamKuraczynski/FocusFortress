package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing the passcode used for protected mode.
 *
 * This entity is stored in the "passcode_table" in the Room database.
 *
 * @property id The unique identifier for the passcode entry. Default is 1 as only one passcode is stored.
 * @property passcode The actual passcode string.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.room.Entity
 * @see androidx.room.PrimaryKey
 */
@Entity(tableName = "passcode_table")
data class Passcode(
    @PrimaryKey val id: Int = 1, // one pass only
    val passcode: String
)