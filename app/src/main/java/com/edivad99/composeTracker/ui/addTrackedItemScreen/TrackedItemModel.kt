package com.edivad99.composeTracker.ui.addTrackedItemScreen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.edivad99.composeTracker.utils.TrackerSnackBar
import com.google.firebase.storage.StorageReference
import domain.Category
import domain.DataResponse
import domain.TrackedItem
import domain.TrackedSubItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import repositories.TrackedItemRepository
import java.util.UUID

class TrackedItemModel(
    private val repository: TrackedItemRepository,
    private val storageReference: StorageReference,
    val categories: List<Category>
) : StateScreenModel<TrackedItemState>(
    TrackedItemState(name = "", category = Category.default(), coverUri = null)
) {

    fun onNameChange(value: String) {
        mutableState.value = mutableState.value.copy(name = value)
    }

    fun onCategoryChange(value: Category) {
        mutableState.value = mutableState.value.copy(category = value)
    }

    fun onCoverChange(value: Uri?) {
        mutableState.value = mutableState.value.copy(coverUri = value)
    }

    private var _sendState: DataResponse<TrackedItem>? by mutableStateOf(null)
    val sendState = _sendState


    fun addTrackedItem(dismissLambda: () -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val data = state.value
            val coverUri = data.coverUri?.let {
                val coverUri = "images/${UUID.randomUUID()}"
                storageReference.child(coverUri)
                    .putFile(it).await()
                coverUri
            }

            val added = repository.addItem(
                TrackedItem(
                    name = data.name,
                    coverUrl = coverUri,
                    category = data.category,
                    items = listOf(
                        TrackedSubItem(
                            name = "", orderWeight = 0, completedNumber = 0, isCompleted = false
                        )
                    )
                )
            )
            if (added) dismissLambda()
            else TrackerSnackBar.pushMessage("Error adding item")

        }
    }
}


data class TrackedItemState(
    val name: String,
    val category: Category,
    val coverUri: Uri?
)