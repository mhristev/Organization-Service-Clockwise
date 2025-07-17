package com.clockwise.orgservice.domain.dto

/**
 * DTO specifically for business unit address and location information
 * Used for location-based clock-in functionality
 */
data class BusinessUnitAddressDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val allowedRadius: Double = 200.0
)
