package com.adamkuraczynski.focusfortress.strictness

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StrictnessViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("focus_fortress_prefs", Context.MODE_PRIVATE)

    private val _strictnessLevel = MutableStateFlow(prefs.getString("strictness_level", "Normal") ?: "Normal")
    val strictnessLevel: StateFlow<String> = _strictnessLevel

    fun setStrictnessLevel(level: String) {
        prefs.edit().putString("strictness_level", level).apply()
        _strictnessLevel.value = level
    }
}