package com.adamkuraczynski.focusfortress.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedKeyword
import com.adamkuraczynski.focusfortress.FocusFortressApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class BlockKeywordViewModel : ViewModel() {

    private val blockedKeywordDao = FocusFortressApp.database.blockedKeywordDao()

    val blockedKeywords: Flow<List<BlockedKeyword>> = blockedKeywordDao.getBlockedKeywords()

    private val _uiEvents = MutableSharedFlow<ToastEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun addKeyword(keyword: String) {
        viewModelScope.launch {
            val normalizedKeyword = keyword.trim().lowercase()
            if (normalizedKeyword.isNotEmpty()) {
                val blockedKeyword = BlockedKeyword(normalizedKeyword)
                blockedKeywordDao.insertBlockedKeyword(blockedKeyword)
                val count = blockedKeywordDao.getBlockedKeywordsCount()
                _uiEvents.emit(ToastEvent.ShowToast("keyword", count))
            }
        }
    }

    
    fun removeKeyword(blockedKeyword: BlockedKeyword) {
        viewModelScope.launch {
            blockedKeywordDao.deleteBlockedKeyword(blockedKeyword)
        }
    }
}