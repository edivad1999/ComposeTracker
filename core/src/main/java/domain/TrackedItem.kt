package domain

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.random.Random

@Serializable
data class TrackedItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Test",
    val coverUrl: String? = "Test",
    val category: Category? = Random.nextBoolean().let {
        if (it) Category()
        else null
    },
    val items: List<TrackedSubItem> = emptyList()
)

@Serializable
data class Category(
    val name: String = "Test",
    val orderWeight: Int = 0
) {
    companion object {
        fun default() = Category("Default", -99)
    }
}

@Serializable
data class TrackedSubItem(
    val name: String, val orderWeight: Int, val completedNumber: Int, val isCompleted: Boolean
) {
    constructor() : this("", 0, completedNumber = 0, isCompleted = false)

}