package com.edivad_99.compose_tracker.ui.home

import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import domain.Category
import domain.DataResponse
import domain.TrackedSubItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import repositories.StorageRepository
import repositories.TrackedItemRepository
import java.util.UUID


class HomeScreenModel(repository: TrackedItemRepository, storageRepository: StorageRepository) :
    ScreenModel {


    val items =
        repository
            .getItems()
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), DataResponse.Loading)

    fun reload() {

    }
}


