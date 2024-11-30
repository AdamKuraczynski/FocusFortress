package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Data class representing a blocked website.
 *
 * This entity is stored in the "blocked_websites" table in the Room database.
 *
 * @property domain The domain of the website to block.
 * @property id A unique identifier for the blocked website.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.room.Entity
 * @see androidx.room.PrimaryKey
 */
@Entity(tableName = "blocked_websites")
data class BlockedWebsite(
    @PrimaryKey val domain: String,
    val id: String = UUID.randomUUID().toString()
)