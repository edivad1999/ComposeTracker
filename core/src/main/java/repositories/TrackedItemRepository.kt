package repositories

import domain.DataResponse
import domain.TrackedItem
import kotlinx.coroutines.flow.Flow

interface TrackedItemRepository {
    fun getItems(): Flow<DataResponse<List<TrackedItem>>>

    fun getItemById(id: String): Flow<DataResponse<TrackedItem>>

    suspend fun addItem(trackedItem: TrackedItem):Boolean

    fun removeItem(trackedItem: TrackedItem):Flow<DataResponse<Unit>>

    fun updateItem(trackedItem: TrackedItem):Flow<DataResponse<TrackedItem>>

}