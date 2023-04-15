package firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.snapshots
import domain.DataResponse
import domain.TrackedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import repositories.TrackedItemRepository

class TrackedItemRepositoryImpl(private val collection: CollectionReference) :
    TrackedItemRepository {

    override fun getItems() = collection.snapshots().map {
        runCatching {
            if (!it.isEmpty) {
                val list = it.toObjects(TrackedItem::class.java).toList()
                DataResponse.Success(list)
            } else {
                DataResponse.Success(emptyList<TrackedItem>())
            }
        }.getOrElse { DataResponse.Error("Error") }
    }

    override fun getItemById(id: String) = flow {
        emit(DataResponse.Loading)
        runCatching {
            collection.whereEqualTo(TrackedItem::id.name, id).get().await()
                .toObjects(TrackedItem::class.java).firstOrNull()
                ?: throw Error("Could not retrieve an element with this id")
        }.onSuccess {
            emit(DataResponse.Success(it))
        }.onFailure {
            emit(DataResponse.Error(it.localizedMessage ?: "Error"))
        }
    }

    override suspend fun addItem(trackedItem: TrackedItem) = runCatching {
        val id = collection.document().id
        val trackedItemToInsert = trackedItem.copy(
            id = id
        )
        collection.document(id).set(trackedItemToInsert).await()
        true
    }.getOrElse { false }


    override fun removeItem(trackedItem: TrackedItem) = flow {
        emit(DataResponse.Loading)
        runCatching {
            collection.document(trackedItem.id).delete().await()
            Unit
        }.onSuccess {
            emit(DataResponse.Success(it))
        }.onFailure {
            emit(DataResponse.Error(it.localizedMessage ?: "Error"))
        }
    }

    override fun updateItem(trackedItem: TrackedItem) = flow {
        emit(DataResponse.Loading)
        runCatching {
            collection.document(trackedItem.id).set(trackedItem).await()
            trackedItem
        }.onSuccess {
            emit(DataResponse.Success(it))
        }.onFailure {
            emit(DataResponse.Error(it.localizedMessage ?: "Error"))
        }
    }
}