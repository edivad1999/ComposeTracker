package domain

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TrackedItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Test",
    val coverUrl: String = "Test",
    val category: Category = Category(),
    val items: List<TrackedSubItem> = emptyList()
)

@Serializable
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String  = "Default",
    val orderWeight: Int = 0
)

@Serializable
data class TrackedSubItem(
    val name: String,
    val orderWeight: Int,
    val completedNumber: Int,
    val isCompleted: Boolean
)