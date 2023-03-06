package com.edivad_99.compose_tracker.ui.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import com.edivad_99.compose_tracker.ui.common.CommonErrorScreen
import com.edivad_99.compose_tracker.ui.common.CommonLoadingScreen
import com.edivad_99.compose_tracker.ui.common.PullRefresh
import com.edivad_99.compose_tracker.ui.common.TrackerScaffold
import domain.DataResponse
import domain.TrackedItem
import org.koin.androidx.compose.get

class HomeScreen : Screen {

  @Composable
  override fun Content() {

    TrackerScaffold(topBarTitle = "Home") {
      val model: HomeScreenModel = get()
      val state by model.items.collectAsState()

      HomeScreenComponent(state = state) { model.reload() }
    }
  }
}

@Composable
fun HomeScreenComponent(state: DataResponse<List<TrackedItem>>, onReload: () -> Unit) {
  when (state) {
    is DataResponse.Error -> CommonErrorScreen(message = state.message) { onReload() }
    is DataResponse.Loading -> CommonLoadingScreen()
    is DataResponse.Success -> HomeScreenSuccess(state, onReload)
  }
}

@Composable
fun HomeScreenSuccess(state: DataResponse.Success<List<TrackedItem>>, onReload: () -> Unit) {
  PullRefresh(refreshing = false, onRefresh = onReload, enabled = true) {
    LazyColumn() { items(state.data) { Text(it.toString()) } }
  }
}
