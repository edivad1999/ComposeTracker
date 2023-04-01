package com.edivad_99.compose_tracker.di

import com.edivad_99.compose_tracker.ui.home.HomeScreenModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import firestore.TrackedItemRepositoryImpl
import org.koin.dsl.module
import repositories.TrackedItemRepository

private const val TRACKED_ITEMS_COLLECTION = "tracked_item"

object DiModules {
    val commonModule
        get() = module {
            single {
                val collection = Firebase.firestore.collection(TRACKED_ITEMS_COLLECTION)
                val repo: TrackedItemRepository = TrackedItemRepositoryImpl(collection)
                repo
            }
            single {
                Firebase.storage.reference
            }

        }

    val modelsModule
        get() = module { factory { HomeScreenModel(get()) } }
}
