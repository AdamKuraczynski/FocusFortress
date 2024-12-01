package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow


@Dao
interface BlockedAppDao {

    
    @Query("SELECT * FROM blocked_apps")
    fun getBlockedApps(): Flow<List<BlockedApp>>

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(blockedApp: BlockedApp)

    
    @Delete
    suspend fun deleteBlockedApp(blockedApp: BlockedApp)

    
    @Query("SELECT EXISTS(SELECT 1 FROM blocked_apps WHERE packageName = :packageName)")
    suspend fun isAppBlocked(packageName: String): Boolean
}