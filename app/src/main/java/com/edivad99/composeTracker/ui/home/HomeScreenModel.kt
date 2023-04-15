package com.edivad99.composeTracker.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import domain.Category
import domain.DataResponse
import domain.TrackedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import repositories.TrackedItemRepository


class HomeScreenModel(private val repository: TrackedItemRepository) :
    StateScreenModel<HomeScreenState>(HomeScreenState.Loading) {

    var isAdding by mutableStateOf(false)


    init {
        coroutineScope.launch(Dispatchers.IO) {
            repository.getItems().mapToHomeScreenState().collectLatest { newValue ->
                mutableState.tryEmit(newValue)
            }
        }
    }

    fun addTrackedItem() {
        isAdding = true
    }
    fun onAddFinished(){
        isAdding =false
    }

    fun reload() {
        coroutineScope.launch(Dispatchers.IO) {
            repository.getItems().mapToHomeScreenState().filter {
//                it is DataResponse.Success || it is DataResponse.Error
                true
            }.collectLatest {
                mutableState.tryEmit(it)
            }
        }
    }
}


sealed interface HomeScreenState {
    object Loading : HomeScreenState
    data class Error(
        val message: String
    ) : HomeScreenState

    data class Success(val data: List<UiCategory>) : HomeScreenState {

    }

}

internal fun Flow<DataResponse<List<TrackedItem>>>.mapToHomeScreenState() = map {
    it.toHomeScreenState()
}

fun Iterable<Category>.sortByWeight() = this.sortedBy { it.orderWeight }

fun DataResponse<List<TrackedItem>>.toHomeScreenState(): HomeScreenState = when (this) {
    is DataResponse.Loading -> HomeScreenState.Loading
    is DataResponse.Error -> HomeScreenState.Error(message)
    is DataResponse.Success -> HomeScreenState.Success(buildList<UiCategory> {
        data.groupBy {
            it.category ?: Category.default()
        }.forEach {
            add(UiCategory(it.key, it.value))
        }

        if (this.none { uiCat ->
                uiCat.category.name == Category.default().name
            }) add(
            UiCategory(
                Category.default(), emptyList()
            )
        )
    }.sortedBy { it.category.orderWeight }
    )
}

data class UiCategory(
    val category: Category, val list: List<TrackedItem>
)