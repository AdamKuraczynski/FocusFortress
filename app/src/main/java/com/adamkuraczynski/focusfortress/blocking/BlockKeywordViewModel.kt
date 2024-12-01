package com.adamkuraczynski.focusfortress.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedKeyword
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class BlockKeywordViewModel : ViewModel() {

    private val blockedKeywordDao = FocusFortressApp.database.blockedKeywordDao()

    val blockedKeywords: Flow<List<BlockedKeyword>> = blockedKeywordDao.getBlockedKeywords()

    
    fun addKeyword(keyword: String) {
        viewModelScope.launch {
            val normalizedKeyword = keyword.trim().lowercase()
            if (normalizedKeyword.isNotEmpty()) {
                val blockedKeyword = BlockedKeyword(normalizedKeyword)
                blockedKeywordDao.insertBlockedKeyword(blockedKeyword)
            }
        }
    }

    
    fun removeKeyword(blockedKeyword: BlockedKeyword) {
        viewModelScope.launch {
            blockedKeywordDao.deleteBlockedKeyword(blockedKeyword)
        }
    }
}