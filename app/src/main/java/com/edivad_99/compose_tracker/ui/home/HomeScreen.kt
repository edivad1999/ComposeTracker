package com.edivad_99.compose_tracker.ui.home

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import com.edivad_99.compose_tracker.ui.common.CommonErrorScreen
import com.edivad_99.compose_tracker.ui.common.CommonLoadingScreen
import com.edivad_99.compose_tracker.ui.common.PullRefresh
import com.edivad_99.compose_tracker.ui.common.TrackerScaffold
import com.google.firebase.storage.StorageReference
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.ImageState
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.coil.CoilImageState
import com.skydoves.landscapist.coil.LocalCoilImageLoader
import com.skydoves.landscapist.components.ImageComponent
import com.skydoves.landscapist.components.rememberImageComponent
import domain.DataResponse
import domain.TrackedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import repositories.StorageRepository

class HomeScreen() : Screen {

    @Composable
    override fun Content() {

        val model: HomeScreenModel = get()
        val state by model.items.collectAsState()
        Column() {
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.data) {

                FireStorageCoilImage(
                    fileReference = it.coverUrl,
                    get(),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop, alignment = Alignment.Center
                    ), failure = {
                        Text(text = it.reason?.localizedMessage ?: "")
                    }
                )
                Text(text = it.toString())
                Spacer(Modifier.size(8.dp))
            }
        }
    }
}


@Composable
fun FireStorageCoilImage(
    fileReference: String,
    storageReference: StorageReference,
    modifier: Modifier = Modifier,
    imageLoader: @Composable () -> ImageLoader = {
        LocalCoilImageLoader.current ?: LocalContext.current.imageLoader
    },
    component: ImageComponent = rememberImageComponent {},
    requestListener: (() -> ImageRequest.Listener)? = null,
    imageOptions: ImageOptions = ImageOptions(),
    onImageStateChanged: (CoilImageState) -> Unit = {},
    @DrawableRes previewPlaceholder: Int = 0,
    loading: @Composable (BoxScope.(imageState: CoilImageState.Loading) -> Unit)? = null,
    success: @Composable (BoxScope.(imageState: CoilImageState.Success) -> Unit)? = null,
    failure: @Composable (BoxScope.(imageState: CoilImageState.Failure) -> Unit)? = null,
) {

    var uri: DataResponse<Uri> by remember {
        mutableStateOf(DataResponse.Loading)
    }
    LaunchedEffect(Unit) {
        val fetchUri = withContext(Dispatchers.IO) {
            FireStorageCacheProvider.getFromCache(storageReference, fileReference)
        }
        withContext(Dispatchers.Main) {
            uri = fetchUri
        }

    }
    when (val uri = uri) {
        is DataResponse.Success -> CoilImage(
            imageModel = {
                uri.data
            },
            modifier = modifier,
            imageLoader = imageLoader,
            component = component,
            requestListener = requestListener,
            imageOptions = imageOptions,
            onImageStateChanged = onImageStateChanged,
            previewPlaceholder = previewPlaceholder,
            loading = loading,
            success = success,
            failure = failure
        )

        is DataResponse.Error -> Box(modifier) {
            failure?.invoke(this, CoilImageState.Failure(null, Throwable(uri.message)))
        }

        is DataResponse.Loading -> Box(modifier) {
            loading?.invoke(this, CoilImageState.Loading)
        }
    }
}

object FireStorageCacheProvider {
    private val cache = mutableMapOf<String, Uri>()
    suspend fun getFromCache(
        storageReference: StorageReference,
        childPath: String
    ): DataResponse<Uri> {
        val childPath = "images/cartello_viario.jpg"
        val uri = cache[childPath]
        return if (uri != null) {
            println("using cached URI $uri for FILE $childPath")
            DataResponse.Success(uri)
        } else {
            runCatching {
                val uri = storageReference.child(childPath).downloadUrl.await()

                cache[childPath] = uri
                println("got URI $uri for FILE $childPath")

                DataResponse.Success(uri)
            }.getOrElse {
                DataResponse.Error(it.localizedMessage ?: "Error in fetching image")
            }
        }

    }

}