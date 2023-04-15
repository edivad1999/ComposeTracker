package com.edivad99.composeTracker.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonLoadingScreen(modifier: Modifier = Modifier, onReload: (() -> Unit)?=null) {
  PullRefresh(refreshing = true, onRefresh = onReload ?: {}, enabled = true) {
    Text(text = "Loading...")
  }

}
