package repositories

import android.net.Uri
import domain.DataResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    suspend fun awaitGetFile(path: String): DataResponse<Uri>
}