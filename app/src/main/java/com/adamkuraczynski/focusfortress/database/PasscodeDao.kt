package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing database operations on the passcode.
 *
 * This interface defines methods for inserting, retrieving, and deleting the passcode
 * in the database.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.2
 *
 * @see androidx.room.Dao
 * @see kotlinx.coroutines.flow.Flow
 */
@Dao
interface PasscodeDao {

    /**
     * Inserts or updates the passcode in the database.
     *
     * @param passcodeEntity The [Passcode] entity to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasscode(passcodeEntity: Passcode)

    /**
     * Retrieves the passcode from the database.
     *
     * @return A [Flow] emitting the [Passcode], or `null` if not set.
     */
    @Query("SELECT * FROM passcode_table WHERE id = 1")
    fun getPasscode(): Flow<Passcode?>

    /**
     * Deletes the passcode from the database.
     */
    @Query("DELETE FROM passcode_table WHERE id = 1")
    suspend fun deletePasscode()
}