package storage

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import domain.DataResponse
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import repositories.StorageRepository
import java.io.File

class StorageFileProvider(
    private val storageReference: StorageReference,
) : StorageRepository {


    override suspend fun awaitGetFile(path: String): DataResponse<Uri> = runCatching {
        val uri = storageReference.child("images/cartello_viario.jpg").downloadUrl.await()
        println("got URI $uri for FILE $path")

        DataResponse.Success(uri)
    }.getOrElse {
        DataResponse.Error(it.localizedMessage ?: "Error in fetching image")
    }


}