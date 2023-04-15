package com.edivad99.composeTracker.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil.request.ImageRequest
import com.edivad99.composeTracker.R
import com.edivad99.composeTracker.theme.components.toTextComposable
import com.edivad99.composeTracker.ui.addTrackedItemScreen.AddTrackedItemScreen
import com.edivad99.composeTracker.ui.common.CommonErrorScreen
import com.edivad99.composeTracker.ui.common.CommonLoadingScreen
import com.edivad99.composeTracker.ui.common.PullRefresh
import com.google.firebase.storage.StorageReference
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import domain.Category
import domain.TrackedItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

class HomeScreen() : Screen {

    @Composable
    override fun Content() {

        val model: HomeScreenModel = get()
        val state by model.state.collectAsState()
        Column() {
            HomeScreenComponent(
                state = state,
                isAddingTrackedItem = model.isAdding,
                onReload = model::reload,
                onFabClick = model::addTrackedItem,
                onAddFinished = model::onAddFinished
            )

        }
    }
}

@Composable
fun IsAddingComponent(
    categories: List<Category>, isAdding: Boolean, onDismissRequest: () -> Unit
) {
    if (isAdding) {
        AddTrackedItemScreen(availableCategories = categories, onDismissRequest = onDismissRequest)
    }
}

@Composable
fun HomeScreenComponent(
    state: HomeScreenState,
    isAddingTrackedItem: Boolean,
    onReload: () -> Unit,
    onFabClick: () -> Unit,
    onAddFinished: () -> Unit
) {
    when (state) {
        is HomeScreenState.Error -> CommonErrorScreen(message = state.message) { onReload() }
        is HomeScreenState.Loading -> CommonLoadingScreen()
        is HomeScreenState.Success -> {
            val categories = remember(state) {
                state.data.map {
                    it.category
                }
            }
            IsAddingComponent(
                categories = categories,
                isAdding = isAddingTrackedItem,
                onDismissRequest = onAddFinished
            )
            HomeScreenSuccess(state, onReload, onFabClick)
        }
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
                ScrollableTabRow(
                    edgePadding = 8.dp,
                    selectedTabIndex = kotlin.runCatching { pagerState.currentPage }.onFailure {
                        coroutine.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }.getOrNull() ?: pagerState.initialPage,
                ) {
                    state.data.map { it.category }.forEachIndexed { index, it ->
                        Tab(pagerState.currentPage == index, onClick = {
                            if (pagerState.currentPage != index) {
                                coroutine.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        }) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(50.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                Text(text = it.name, modifier = Modifier)
                            }
                        }
                    }
                }
                HorizontalPager(
                    pageCount = state.data.size, state = pagerState
                ) { pageNumber ->
                    val selectedCategory = state.data[pageNumber]
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
                            Text(text = "No elements here")
                        }
                    }


                }
            }
            FloatingActionButton(
                onClick = onFabClick, modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
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
            val image = trackedItem.coverUrl
            if (image != null) FireStorageCoilImage(
                get<StorageReference>().child(image),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop, alignment = Alignment.Center
                ),
                loading = {
                    CircularProgressIndicator(Modifier.heightIn(min = 200.dp))
                },
                failure = {
                   it.reason?.localizedMessage?.toTextComposable()
                },
            ) else PlaceHolderImage(Modifier.heightIn(min = 200.dp))

            Text(
                text = trackedItem.name,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    Spacer(Modifier.size(8.dp))
}


@Composable
fun PlaceHolderImage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val drawable = remember {
        context.getDrawable(R.drawable.placeholder_image)
    }
    CoilImage(
        modifier = modifier,
        imageModel = {
            drawable
        }, imageOptions = ImageOptions(
            contentScale = ContentScale.Crop, alignment = Alignment.Center
        )
    )
}

