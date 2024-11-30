package com.adamkuraczynski.focusfortress.blocking

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * Factory class for creating instances of [BlockAppViewModel].
 *
 * This factory is necessary to pass the [Application] context to the ViewModel.
 *
 * @param application The [Application] context used by the ViewModel.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see androidx.lifecycle.ViewModelProvider.Factory
 */
class BlockAppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(BlockAppViewModel::class.java)) {
            return BlockAppViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}