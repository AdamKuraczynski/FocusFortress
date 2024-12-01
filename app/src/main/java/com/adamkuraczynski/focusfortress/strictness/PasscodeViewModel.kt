package com.adamkuraczynski.focusfortress.strictness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import com.adamkuraczynski.focusfortress.database.Passcode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PasscodeViewModel : ViewModel() {

    private val passcodeDao = FocusFortressApp.database.passcodeDao()

    val passcodeState: StateFlow<Passcode?> = passcodeDao.getPasscode()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    
    fun savePasscode(passcode: String) {
        viewModelScope.launch {
            val passcodeEntity = Passcode(passcode = passcode)
            passcodeDao.insertPasscode(passcodeEntity)
        }
    }
}