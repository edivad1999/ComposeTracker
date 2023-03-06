package com.edivad_99.compose_tracker.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerAppBar(title: String) {

  TopAppBar(title = { Text(text = title) })
}
