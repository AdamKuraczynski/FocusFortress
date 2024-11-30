package com.adamkuraczynski.focusfortress.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedWebsite
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel responsible for managing the blocked websites.
 *
 * Provides functionality to block and unblock websites by domain.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.lifecycle.ViewModel
 */
class BlockWebsiteViewModel : ViewModel() {

    private val blockedWebsiteDao = FocusFortressApp.database.blockedWebsiteDao()

    val blockedWebsites: Flow<List<BlockedWebsite>> = blockedWebsiteDao.getBlockedWebsites()

    /**
     * Blocks a website by adding its domain to the blocked list.
     *
     * @param domain The domain or URL of the website to block.
     */
    fun blockWebsite(domain: String) {
        viewModelScope.launch {
            val normalizedDomain = domain.trim().lowercase(Locale.getDefault())
            val blockedWebsite = BlockedWebsite(normalizedDomain)
            blockedWebsiteDao.insertBlockedWebsite(blockedWebsite)
        }
    }

    /**
     * Unblocks a website by removing it from the blocked list.
     *
     * @param blockedWebsite The [BlockedWebsite] to unblock.
     */
    fun unblockWebsite(blockedWebsite: BlockedWebsite) {
        viewModelScope.launch {
            blockedWebsiteDao.deleteBlockedWebsite(blockedWebsite)
        }
    }
}