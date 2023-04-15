package com.edivad99.composeTracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.edivad99.composeTracker.theme.ComposeTrackerTheme
import com.edivad99.composeTracker.ui.ComposeTrackerApp

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    setContent { ComposeTrackerTheme() { ComposeTrackerApp() } }
  }
}
