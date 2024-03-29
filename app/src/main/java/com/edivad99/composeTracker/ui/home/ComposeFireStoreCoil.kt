package com.edivad99.composeTracker.ui.home

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.key.Keyer
import coil.request.ImageRequest
import com.google.firebase.storage.StorageReference
import coil.request.Options
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.coil.CoilImageState
import com.skydoves.landscapist.coil.LocalCoilImageLoader
import com.skydoves.landscapist.components.ImageComponent
import com.skydoves.landscapist.components.rememberImageComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream


class StorageReferenceKeyer : Keyer<StorageReference> {
    override fun key(data: StorageReference, options: Options): String = data.path
}

class StorageReferenceFetcher(
    private val data: StorageReference,
    private val options: Options,
    private val diskCache: DiskCache?
) : Fetcher {

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult {
        val snapshot = readFromDiskCache()
        snapshot?.let {
            // Return the candidate from the cache if it is eligible.
            return SourceResult(
                source = ImageSource(
                    it.data, diskCache!!.fileSystem, data.path, it
                ), mimeType = "image/*", dataSource = DataSource.DISK
            )
        }

        // Slow path: fetch the image from the network.


        var index = 0
        val maxTries = 10
        var byteArray: ByteArray? =
            runCatching { data.getBytes(Long.MAX_VALUE).await() }.getOrNull()

        while (index < maxTries && byteArray == null) {
            delay(500)
            byteArray = runCatching { data.getBytes(Long.MAX_VALUE).await() }.getOrNull()
            index++
        }

        // Write the response to the disk cache then open a new snapshot.

        val byteInputStream = byteArray!!.inputStream()
        writeToDiskCache(byteInputStream)

        return SourceResult(
            dataSource = DataSource.NETWORK,
            source = ImageSource(
                byteArray!!.inputStream()
                    .source().buffer(), options.context
            ),
            mimeType = "image/*"
        )
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun readFromDiskCache(): DiskCache.Snapshot? = diskCache?.get(data.path)

    @OptIn(ExperimentalCoilApi::class)
    private fun writeToDiskCache(source: ByteArrayInputStream) {
        val editor = diskCache?.edit(data.path)
        if (editor != null) {
            try {
                editor.data.let {
                    diskCache?.fileSystem?.write(it) {
                        this.writeAll(source.source())
                    }
                }
                editor.commitAndGet()
            } catch (exception: Exception) {
                editor.abort()
                throw exception
            }
        }
    }

    class Factory : Fetcher.Factory<StorageReference> {

        override fun create(
            data: StorageReference, options: Options, imageLoader: ImageLoader
        ): Fetcher {
            return StorageReferenceFetcher(data, options, imageLoader.diskCache)
        }
    }
}


@Composable
fun FireStorageCoilImage(
    storageReference: StorageReference,
    modifier: Modifier = Modifier,
    component: ImageComponent = rememberImageComponent {},
    requestListener: (() -> ImageRequest.Listener)? = null,
    imageOptions: ImageOptions = ImageOptions(),
    onImageStateChanged: (CoilImageState) -> Unit = {},
    @DrawableRes previewPlaceholder: Int = 0,
    loading: @Composable (BoxScope.(imageState: CoilImageState.Loading) -> Unit)? = null,
    success: @Composable (BoxScope.(imageState: CoilImageState.Success) -> Unit)? = null,
    failure: @Composable (BoxScope.(imageState: CoilImageState.Failure) -> Unit)? = null,
) {
    val imageLoader = rememberComposeFireCoil()
    CompositionLocalProvider(LocalCoilImageLoader provides imageLoader) {
        CoilImage(
            imageModel = {
                storageReference
            },
            modifier = modifier,
            component = component,
            requestListener = requestListener,
            imageOptions = imageOptions,
            onImageStateChanged = onImageStateChanged,
            previewPlaceholder = previewPlaceholder,
            loading = loading,
            success = success,
            failure = failure
        )


    }
}

@Composable
fun rememberComposeFireCoil(): ImageLoader {
    val context = LocalContext.current.applicationContext
    return remember {
        ComposeFireStoreCoil.getImageLoader(context)
    }
}


object ComposeFireStoreCoil {

    private var _imageLoader: ImageLoader? = null

    fun getImageLoader(context: Context): ImageLoader =
        _imageLoader ?: ImageLoader.Builder(context).components {
            add(StorageReferenceFetcher.Factory())
            add(StorageReferenceKeyer())
        }.build().also {
            _imageLoader?.shutdown()
            _imageLoader = it
        }


}
