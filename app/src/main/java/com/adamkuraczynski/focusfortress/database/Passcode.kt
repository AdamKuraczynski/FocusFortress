package com.adamkuraczynski.focusfortress.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "passcode_table")
data class Passcode(
    @PrimaryKey val id: Int = 1, // one pass only
    val passcodeHash: String,
    val salt: String
)