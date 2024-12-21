package com.adamkuraczynski.focusfortress.blocking

sealed class ToastEvent {
    data class ShowToast(val type: String, val count: Int) : ToastEvent()
}