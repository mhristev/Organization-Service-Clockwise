package com.clockwise.orgservice.handler

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException

private val logger = KotlinLogging.logger {}

@ControllerAdvice
class LoggingExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        logger.error(ex) { "Unhandled exception occurred" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "An unexpected error occurred", "message" to (ex.message ?: "Unknown error")))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<Map<String, String>> {
        logger.warn { "Status exception occurred: ${ex.statusCode}, reason: ${ex.reason}" }
        return ResponseEntity.status(ex.statusCode)
            .body(mapOf("error" to "Request error", "message" to (ex.reason ?: "Unknown error")))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        logger.warn(ex) { "Invalid argument provided" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Bad request", "message" to (ex.message ?: "Invalid argument provided")))
    }
} 