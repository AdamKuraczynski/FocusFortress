package com.adamkuraczynski.focusfortress.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedApp
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BlockAppViewModel : ViewModel() {

    private val blockedAppDao = FocusFortressApp.database.blockedAppDao()

    val blockedApps: Flow<List<BlockedApp>> = blockedAppDao.getBlockedApps()

    fun blockApp(packageName: String, appName: String) {
        viewModelScope.launch {
            val blockedApp = BlockedApp(packageName, appName)
            blockedAppDao.insertBlockedApp(blockedApp)
        }
    }

    fun unblockApp(blockedApp: BlockedApp) {
        viewModelScope.launch {
            blockedAppDao.deleteBlockedApp(blockedApp)
        }
    }

//    suspend fun isAppBlocked(packageName: String): Boolean {
//        return blockedAppDao.isAppBlocked(packageName)
//    }

}