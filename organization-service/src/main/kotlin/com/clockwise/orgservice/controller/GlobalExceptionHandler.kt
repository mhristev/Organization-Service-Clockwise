package com.clockwise.orgservice.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.web.ErrorResponse

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("Invalid input: ${ex.message}", HttpStatus.BAD_REQUEST)
    }

//    @ExceptionHandler(IllegalStateException::class)
//    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<String> {
//            return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
//        // Handle other IllegalStateExceptions or return a generic error
//    }

    @ExceptionHandler(OptimisticLockingFailureException::class)
    fun handleOptimisticLockingFailureException(ex: OptimisticLockingFailureException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("Conflict: ${ex.message}", HttpStatus.CONFLICT)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("An error occurred: ${ex.message}", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}