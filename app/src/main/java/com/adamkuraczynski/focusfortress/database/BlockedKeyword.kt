package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Data class representing a blocked keyword.
 *
 * This entity is stored in the "blocked_keywords" table in the Room database.
 *
 * @property keyword The keyword to block.
 * @property id A unique identifier for the blocked keyword.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.room.Entity
 * @see androidx.room.PrimaryKey
 */
@Entity(tableName = "blocked_keywords")
data class BlockedKeyword(
    @PrimaryKey val keyword: String,
    val id: String = UUID.randomUUID().toString()
)