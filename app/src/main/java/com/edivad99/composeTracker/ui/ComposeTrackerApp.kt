package com.edivad99.composeTracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.edivad99.composeTracker.ui.home.HomeScreen
import com.edivad99.composeTracker.utils.TrackerSnackBar

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
            bottomBar = { /* ... */ },
            snackbarHost = {
                val snackbarHostState = remember { SnackbarHostState() }

                val snackBarState = remember { TrackerSnackBar }
                val snackBarData by snackBarState.message.collectAsState()
                LaunchedEffect(snackBarData) {
                    snackBarData?.let { snackbarHostState.showSnackbar(it, withDismissAction = true) }
                }
                SnackbarHost(snackbarHostState) {
                    Snackbar(snackbarData = it)
                }
            }
        )
    }
}

