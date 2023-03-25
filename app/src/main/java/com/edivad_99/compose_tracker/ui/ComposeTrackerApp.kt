package com.edivad_99.compose_tracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.edivad_99.compose_tracker.ui.common.TrackerScaffold
import com.edivad_99.compose_tracker.ui.home.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeTrackerApp() {
    Navigator(HomeScreen()) { navigator ->
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = {
                        Text(
                            text = navigator.lastItem.javaClass.simpleName
                        )
                    })
            },
            content = {
                Column(modifier = Modifier.padding(it)) {
                    CurrentScreen()
                }
            },
            bottomBar = { /* ... */ }
        )
    }
}

