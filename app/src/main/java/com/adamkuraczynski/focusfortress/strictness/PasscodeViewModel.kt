package com.adamkuraczynski.focusfortress.strictness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import com.adamkuraczynski.focusfortress.database.Passcode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the passcode in the FocusFortress application.
 *
 * This ViewModel interacts with the database to save and retrieve the passcode.
 * It provides a state flow for observing the passcode state.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.5
 *
 * @see com.adamkuraczynski.focusfortress.database.PasscodeDao
 * @see kotlinx.coroutines.flow.StateFlow
 */
class PasscodeViewModel : ViewModel() {

    private val passcodeDao = FocusFortressApp.database.passcodeDao()

    val passcodeState: StateFlow<Passcode?> = passcodeDao.getPasscode()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    /**
     * Saves the passcode to the database.
     *
     * @param passcode The passcode string to save.
     */
    fun savePasscode(passcode: String) {
        viewModelScope.launch {
            val passcodeEntity = Passcode(passcode = passcode)
            passcodeDao.insertPasscode(passcodeEntity)
        }
    }
}