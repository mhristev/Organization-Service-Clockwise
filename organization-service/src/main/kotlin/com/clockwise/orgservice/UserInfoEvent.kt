package com.clockwise.orgservice

import java.time.LocalDateTime

data class UserInfoRequest(
    val userId: String,
    val requestId: String = generateRequestId(),
    val requestedBy: String = "organization-service",
    val timestamp: String = LocalDateTime.now().toString()
)

data class UserInfoResponse(
    val userId: String,
    val requestId: String,
    val shiftId: String? = null,
    val firstName: String?,
    val lastName: String?,
    val found: Boolean,
    val errorMessage: String? = null
)

private fun generateRequestId(): String {
    return "org-svc-${System.currentTimeMillis()}-${(Math.random() * 1000).toInt()}"
}