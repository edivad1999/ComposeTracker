package com.edivad99.composeTracker.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonErrorScreen(
    modifier: Modifier = Modifier,
    message: String,
    onReload: (() -> Unit)?,
) {
  if (onReload != null) {
    PullRefresh(refreshing = true, onRefresh = { onReload() }, enabled = true) {
      Text(text = message)
    }
  } else PullRefresh(refreshing = true, onRefresh = {}, enabled = false) { Text(text = message) }
}
