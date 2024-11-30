package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing database operations on blocked keywords.
 *
 * This interface defines methods for querying, inserting, and deleting blocked keywords
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
interface BlockedKeywordDao {

    /**
     * Retrieves all blocked keywords from the database.
     *
     * @return A [Flow] emitting a list of [BlockedKeyword] objects.
     */
    @Query("SELECT * FROM blocked_keywords")
    fun getBlockedKeywords(): Flow<List<BlockedKeyword>>

    /**
     * Inserts a [BlockedKeyword] into the database.
     *
     * @param blockedKeyword The [BlockedKeyword] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedKeyword(blockedKeyword: BlockedKeyword)

    /**
     * Deletes a [BlockedKeyword] from the database.
     *
     * @param blockedKeyword The [BlockedKeyword] to delete.
     */
    @Delete
    suspend fun deleteBlockedKeyword(blockedKeyword: BlockedKeyword)

    /**
     * Checks if a keyword is blocked.
     *
     * @param keyword The keyword to check.
     * @return `true` if the keyword is blocked; `false` otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM blocked_keywords WHERE keyword = :keyword)")
    suspend fun isKeywordBlocked(keyword: String): Boolean
}