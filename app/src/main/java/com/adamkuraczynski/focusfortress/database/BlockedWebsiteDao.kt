package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing database operations on blocked websites.
 *
 * This interface defines methods for querying, inserting, and deleting blocked websites
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
interface BlockedWebsiteDao {

    /**
     * Retrieves all blocked websites from the database.
     *
     * @return A [Flow] emitting a list of [BlockedWebsite] objects.
     */
    @Query("SELECT * FROM blocked_websites")
    fun getBlockedWebsites(): Flow<List<BlockedWebsite>>

    /**
     * Inserts a [BlockedWebsite] into the database.
     *
     * @param blockedWebsite The [BlockedWebsite] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedWebsite(blockedWebsite: BlockedWebsite)

    /**
     * Deletes a [BlockedWebsite] from the database.
     *
     * @param blockedWebsite The [BlockedWebsite] to delete.
     */
    @Delete
    suspend fun deleteBlockedWebsite(blockedWebsite: BlockedWebsite)

    /**
     * Checks if a website is blocked based on its domain.
     *
     * @param domain The domain of the website to check.
     * @return `true` if the website is blocked; `false` otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM blocked_websites WHERE domain = :domain)")
    suspend fun isWebsiteBlocked(domain: String): Boolean

}