package com.edivad_99.compose_tracker.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonLoadingScreen(modifier: Modifier = Modifier, onReload: (() -> Unit)?=null) {
  PullRefresh(refreshing = true, onRefresh = onReload ?: {}, enabled = true) {
    Text(text = "Loading...")
  }

}
