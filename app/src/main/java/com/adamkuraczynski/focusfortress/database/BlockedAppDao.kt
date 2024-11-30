package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing database operations on blocked apps.
 *
 * This interface defines methods for querying, inserting, and deleting blocked applications
 * in the database.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.room.Dao
 * @see kotlinx.coroutines.flow.Flow
 */
@Dao
interface BlockedAppDao {

    /**
     * Retrieves all blocked applications from the database.
     *
     * @return A [Flow] emitting a list of [BlockedApp] objects.
     */
    @Query("SELECT * FROM blocked_apps")
    fun getBlockedApps(): Flow<List<BlockedApp>>

    /**
     * Inserts a [BlockedApp] into the database.
     *
     * @param blockedApp The [BlockedApp] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(blockedApp: BlockedApp)

    /**
     * Deletes a [BlockedApp] from the database.
     *
     * @param blockedApp The [BlockedApp] to delete.
     */
    @Delete
    suspend fun deleteBlockedApp(blockedApp: BlockedApp)

    /**
     * Checks if an application is blocked based on its package name.
     *
     * @param packageName The package name of the application to check.
     * @return `true` if the app is blocked; `false` otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM blocked_apps WHERE packageName = :packageName)")
    suspend fun isAppBlocked(packageName: String): Boolean
}