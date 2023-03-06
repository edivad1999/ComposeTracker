package com.edivad_99.compose_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.edivad_99.compose_tracker.theme.ComposeTrackerTheme
import com.edivad_99.compose_tracker.ui.ComposeTrackerApp

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    setContent { ComposeTrackerTheme() { ComposeTrackerApp() } }
  }
}
