package com.edivad_99.compose_tracker.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.edivad_99.compose_tracker.ui.home.HomeScreen

@Composable
fun ComposeTrackerApp() {
  Navigator(HomeScreen())
}
