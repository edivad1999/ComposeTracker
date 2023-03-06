package com.edivad_99.compose_tracker.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import domain.DataResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import repositories.TrackedItemRepository

class HomeScreenModel(repository: TrackedItemRepository) : ScreenModel {

  val items =
      repository
          .getItems()
          .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), DataResponse.Loading)

    fun reload(){

    }
}
