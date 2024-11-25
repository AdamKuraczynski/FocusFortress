package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "blocked_keywords")
data class BlockedKeyword(
    @PrimaryKey val keyword: String,
    val id: String = UUID.randomUUID().toString()
)