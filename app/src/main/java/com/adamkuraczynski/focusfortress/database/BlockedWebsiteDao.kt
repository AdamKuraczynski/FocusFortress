package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface BlockedWebsiteDao {

    
    @Query("SELECT * FROM blocked_websites")
    fun getBlockedWebsites(): Flow<List<BlockedWebsite>>

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedWebsite(blockedWebsite: BlockedWebsite)

    
    @Delete
    suspend fun deleteBlockedWebsite(blockedWebsite: BlockedWebsite)

    
    @Query("SELECT EXISTS(SELECT 1 FROM blocked_websites WHERE domain = :domain)")
    suspend fun isWebsiteBlocked(domain: String): Boolean


    @Query("SELECT COUNT(*) FROM blocked_websites")
    suspend fun getBlockedWebsitesCount(): Int
}