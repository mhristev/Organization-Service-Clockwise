package com.clockwise.orgservice

data class BusinessUnitEvent(
    val id: String,
    val name: String,
    val type: EventType
)

enum class EventType {
    CREATED, UPDATED, DELETED
}