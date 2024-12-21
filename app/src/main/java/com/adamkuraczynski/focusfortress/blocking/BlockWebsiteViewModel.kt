package com.adamkuraczynski.focusfortress.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedWebsite
import com.adamkuraczynski.focusfortress.FocusFortressApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale


class BlockWebsiteViewModel : ViewModel() {

    private val blockedWebsiteDao = FocusFortressApp.database.blockedWebsiteDao()

    val blockedWebsites: Flow<List<BlockedWebsite>> = blockedWebsiteDao.getBlockedWebsites()

    private val _uiEvents = MutableSharedFlow<ToastEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun blockWebsite(domain: String) {
        viewModelScope.launch {
            val normalizedDomain = domain.trim().lowercase(Locale.getDefault())
            val blockedWebsite = BlockedWebsite(normalizedDomain)
            blockedWebsiteDao.insertBlockedWebsite(blockedWebsite)
            val count = blockedWebsiteDao.getBlockedWebsitesCount()
            _uiEvents.emit(ToastEvent.ShowToast("website", count))
        }
    }

    
    fun unblockWebsite(blockedWebsite: BlockedWebsite) {
        viewModelScope.launch {
            blockedWebsiteDao.deleteBlockedWebsite(blockedWebsite)
        }
    }
}