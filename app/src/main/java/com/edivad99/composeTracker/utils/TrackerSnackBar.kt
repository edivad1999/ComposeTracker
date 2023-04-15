package com.edivad99.composeTracker.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object TrackerSnackBar {
    private val _message: MutableStateFlow<String?> = MutableStateFlow(null)
    val message get() = _message.asStateFlow()


    fun pushMessage(message: String) {
        _message.tryEmit(message)
    }


}