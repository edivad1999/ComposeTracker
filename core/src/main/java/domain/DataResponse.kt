package domain

sealed interface DataResponse<out T> {

    object Loading:DataResponse<Nothing>

    data class Success<out T>(
        val data: T
    ): DataResponse<T>

    data class Error(
        val message: String
    ): DataResponse<Nothing>
}