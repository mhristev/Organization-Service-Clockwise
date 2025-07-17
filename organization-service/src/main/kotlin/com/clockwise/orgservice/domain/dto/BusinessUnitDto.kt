package com.clockwise.orgservice.domain.dto

data class BusinessUnitDto(
    val id: String? = null,
    val name: String,
    val location: String,
    val description: String,
    val companyId: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val allowedRadius: Double = 200.0
)
