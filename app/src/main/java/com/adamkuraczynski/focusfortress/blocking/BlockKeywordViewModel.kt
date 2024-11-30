package com.adamkuraczynski.focusfortress.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedKeyword
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the blocked keywords.
 *
 * Provides functionality to add and remove keywords from the blocked list.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.lifecycle.ViewModel
 */
class BlockKeywordViewModel : ViewModel() {

    private val blockedKeywordDao = FocusFortressApp.database.blockedKeywordDao()

    val blockedKeywords: Flow<List<BlockedKeyword>> = blockedKeywordDao.getBlockedKeywords()

    /**
     * Adds a keyword to the blocked list.
     *
     * @param keyword The keyword to block.
     */
    fun addKeyword(keyword: String) {
        viewModelScope.launch {
            val normalizedKeyword = keyword.trim().lowercase()
            if (normalizedKeyword.isNotEmpty()) {
                val blockedKeyword = BlockedKeyword(normalizedKeyword)
                blockedKeywordDao.insertBlockedKeyword(blockedKeyword)
            }
        }
    }

    /**
     * Removes a keyword from the blocked list.
     *
     * @param blockedKeyword The [BlockedKeyword] to remove.
     */
    fun removeKeyword(blockedKeyword: BlockedKeyword) {
        viewModelScope.launch {
            blockedKeywordDao.deleteBlockedKeyword(blockedKeyword)
        }
    }
}