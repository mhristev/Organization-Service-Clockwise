package com.clockwise.orgservice.event

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Event sent when consumption items are created, updated, or deleted
 * This allows other services (like User Service) to be notified of changes
 */
data class ConsumptionItemEvent(
    val eventType: ConsumptionItemEventType,
    val consumptionItem: ConsumptionItemEventData,
    val businessUnitId: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class ConsumptionItemEventType {
    CREATED,
    UPDATED,
    DELETED
}

data class ConsumptionItemEventData(
    val id: String,
    val name: String,
    val price: BigDecimal,
    val type: String,
    val businessUnitId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
