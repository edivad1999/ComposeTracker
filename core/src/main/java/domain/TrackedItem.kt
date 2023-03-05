package domain

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TrackedItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Test",
    val coverUrl: String = "Test",
    val categoryId: String = UUID.randomUUID().toString(),
    val items: List<TrackedSubItem> = emptyList()
)

@Serializable
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val orderWeight: Int
)

@Serializable
data class TrackedSubItem(
    val name: String,
    val orderWeight: Int,
    val completedNumber: Int,
    val isCompleted: Boolean
)