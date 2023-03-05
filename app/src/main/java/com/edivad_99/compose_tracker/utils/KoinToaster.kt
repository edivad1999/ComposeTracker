package com.edivad_99.compose_tracker.utils

import android.widget.Toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object KoinToaster : KoinComponent {
    fun showToast(text: String) {
        Toast.makeText(get(), text, Toast.LENGTH_SHORT).show()
    }
}