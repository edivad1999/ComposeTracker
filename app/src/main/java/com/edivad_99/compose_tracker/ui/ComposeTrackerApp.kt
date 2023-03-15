package com.edivad_99.compose_tracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.edivad_99.compose_tracker.ui.common.TrackerScaffold
import com.edivad_99.compose_tracker.ui.home.HomeScreen

@Composable
fun ComposeTrackerApp() {
    Navigator(HomeScreen()) {
        TrackerScaffold(topBarTitle = it.lastItem.key) { padding ->
            Column(Modifier.padding(padding).fillMaxSize()) {
                CurrentScreen()
            }
        }
    }
}
