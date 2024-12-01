package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "blocked_websites")
data class BlockedWebsite(
    @PrimaryKey val domain: String,
    val id: String = UUID.randomUUID().toString()
)