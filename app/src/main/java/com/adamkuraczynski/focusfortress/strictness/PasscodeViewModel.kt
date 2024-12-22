package com.adamkuraczynski.focusfortress.strictness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.FocusFortressApp
import com.adamkuraczynski.focusfortress.database.Passcode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PasscodeViewModel : ViewModel() {

    private val passcodeDao = FocusFortressApp.database.passcodeDao()

    private val passcodeState: StateFlow<Pair<String, String>?> = passcodeDao.getPasscode()
        .map { passcodeEntity ->
            passcodeEntity?.let {
                Pair(it.salt, it.passcodeHash)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)


    fun savePasscode(plaintextPasscode: String) {
        viewModelScope.launch {
            try {
                val salt = HashingUtility.generateSalt()
                val passcodeHash = HashingUtility.hashPasscode(plaintextPasscode, salt)
                val passcodeEntity = Passcode(passcodeHash = passcodeHash, salt = salt)
                passcodeDao.insertPasscode(passcodeEntity)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun verifyPasscode(enteredPasscode: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val storedPasscode = passcodeState.value
            if (storedPasscode != null) {
                val (salt, passcodeHash) = storedPasscode
                val isValid = HashingUtility.verifyPasscode(enteredPasscode, salt, passcodeHash)
                onResult(isValid)
            } else {
                onResult(false)
            }
        }
    }
}