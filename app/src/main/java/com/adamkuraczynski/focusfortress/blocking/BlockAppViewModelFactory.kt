package com.adamkuraczynski.focusfortress.blocking

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class BlockAppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(BlockAppViewModel::class.java)) {
            return BlockAppViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


