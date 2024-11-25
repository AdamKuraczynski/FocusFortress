package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedKeywordDao {

    @Query("SELECT * FROM blocked_keywords")
    fun getBlockedKeywords(): Flow<List<BlockedKeyword>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedKeyword(blockedKeyword: BlockedKeyword)

    @Delete
    suspend fun deleteBlockedKeyword(blockedKeyword: BlockedKeyword)

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_keywords WHERE keyword = :keyword)")
    suspend fun isKeywordBlocked(keyword: String): Boolean
}