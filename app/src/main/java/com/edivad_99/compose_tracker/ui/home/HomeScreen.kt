package com.edivad_99.compose_tracker.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import cafe.adriel.voyager.core.screen.Screen
import com.edivad_99.compose_tracker.ui.common.CommonErrorScreen
import com.edivad_99.compose_tracker.ui.common.CommonLoadingScreen
import com.edivad_99.compose_tracker.ui.common.PullRefresh
import com.edivad_99.compose_tracker.utils.pagerTabIndicatorOffset
import com.google.firebase.storage.StorageReference
import com.skydoves.landscapist.ImageOptions
import domain.TrackedItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

class HomeScreen() : Screen {

    @Composable
    override fun Content() {

        val model: HomeScreenModel = get()
        val state by model.state.collectAsState()
        Column() {
            HomeScreenComponent(
                state = state, onReload = model::reload, onFabClick = model::addTrackedItem
            )

        }
    }
}

@Composable
fun HomeScreenComponent(state: HomeScreenState, onReload: () -> Unit, onFabClick: () -> Unit) {
    when (state) {
        is HomeScreenState.Error -> CommonErrorScreen(message = state.message) { onReload() }
        is HomeScreenState.Loading -> CommonLoadingScreen()
        is HomeScreenState.Success -> HomeScreenSuccess(state, onReload, onFabClick)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenSuccess(
    state: HomeScreenState.Success, onReload: () -> Unit, onFabClick: () -> Unit
) {
    val coroutine = rememberCoroutineScope()
    PullRefresh(refreshing = false, onRefresh = onReload, enabled = true) {
        val pagerState = rememberPagerState()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = kotlin.runCatching { pagerState.currentPage }.onFailure {
                        coroutine.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }.getOrNull()
                        ?: pagerState.initialPage,
                ) {
                    state.data.map { it.category }.forEachIndexed { index, it ->
                        Tab(pagerState.currentPage == index, onClick = {
                            if (pagerState.currentPage != index) {
                                coroutine.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        }) {
                            Text(text = it.name)
                        }
                    }
                }
                HorizontalPager(
                    pageCount = state.data.size, state = pagerState
                ) { pageNumber ->
                    val selectedCategory = state.data.get(pageNumber)
                    val items = selectedCategory.list
                    if (items.isNotEmpty()) LazyVerticalGrid(
                        columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()
                    ) {
                        items(items) {
                            HomeScreenTrackedItem(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(), trackedItem = it
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(text = "Nessun Elemento disponibile")
                        }
                    }


                }
            }
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    }
}

@Composable
fun HomeScreenTrackedItem(modifier: Modifier = Modifier, trackedItem: TrackedItem) {

    Card(modifier = modifier) {
        Column {
            FireStorageCoilImage(
                get<StorageReference>().child("images/cartello_viario.jpg"),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop, alignment = Alignment.Center
                )
            )
            Text(text = trackedItem.toString())
        }
    }

    Spacer(Modifier.size(8.dp))
}