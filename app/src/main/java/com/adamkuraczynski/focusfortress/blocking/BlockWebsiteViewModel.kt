package com.adamkuraczynski.focusfortress.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedWebsite
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Locale

class BlockWebsiteViewModel : ViewModel() {

    private val blockedWebsiteDao = FocusFortressApp.database.blockedWebsiteDao()

    val blockedWebsites: Flow<List<BlockedWebsite>> = blockedWebsiteDao.getBlockedWebsites()

    fun blockWebsite(domain: String) {
        viewModelScope.launch {
            val normalizedDomain = domain.trim().lowercase(Locale.getDefault())
            val blockedWebsite = BlockedWebsite(normalizedDomain)
            blockedWebsiteDao.insertBlockedWebsite(blockedWebsite)
        }
    }

    fun unblockWebsite(blockedWebsite: BlockedWebsite) {
        viewModelScope.launch {
            blockedWebsiteDao.deleteBlockedWebsite(blockedWebsite)
        }
    }
}