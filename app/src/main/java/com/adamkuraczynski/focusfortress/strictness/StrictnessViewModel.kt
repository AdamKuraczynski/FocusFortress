package com.adamkuraczynski.focusfortress.strictness

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for managing the strictness level in the FocusFortress application.
 *
 * This ViewModel provides a state flow for observing the current strictness level
 * and methods to update it. The strictness level is persisted using SharedPreferences.
 *
 * @param application The application context.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see android.content.SharedPreferences
 * @see kotlinx.coroutines.flow.StateFlow
 */
class StrictnessViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("focus_fortress_prefs", Context.MODE_PRIVATE)

    private val _strictnessLevel = MutableStateFlow(prefs.getString("strictness_level", "Normal") ?: "Normal")
    val strictnessLevel: StateFlow<String> = _strictnessLevel

    /**
     * Sets the strictness level and updates the SharedPreferences.
     *
     * @param level The new strictness level to set.
     */
    fun setStrictnessLevel(level: String) {
        prefs.edit().putString("strictness_level", level).apply()
        _strictnessLevel.value = level
    }
}