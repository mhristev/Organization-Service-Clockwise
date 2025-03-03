package com.clockwise.orgservice.config

sealed interface DataError: Error {
    enum class Remote: DataError {
        INVALID_INPUT,
        CONFLICT,
        UNKNOWN
    }
}


//sealed class DataError {
//    data class InvalidInput(val message: String): DataError()
//    data class Conflict(val message: String): DataError()
//    data class Remote(val message: String): DataError()
//}