package com.adamkuraczynski.focusfortress.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface PasscodeDao {

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasscode(passcodeEntity: Passcode)

    
    @Query("SELECT * FROM passcode_table WHERE id = 1")
    fun getPasscode(): Flow<Passcode?>

    
    @Query("DELETE FROM passcode_table WHERE id = 1")
    suspend fun deletePasscode()
}