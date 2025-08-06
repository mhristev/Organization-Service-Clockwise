package com.clockwise.orgservice.domain.dto

data class BusinessUnitDto(
    val id: String? = null,
    val name: String,
    val location: String,
    val description: String,
    val companyId: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val allowedRadius: Double = 200.0,
    val phoneNumber: String? = null,
    val email: String? = null
)

data class BusinessUnitPartialUpdateDto(
    val description: String?,
    val phoneNumber: String?,
    val email: String?
)

data class BusinessUnitFullUpdateDto(
    val name: String,
    val location: String,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val phoneNumber: String?,
    val email: String?
)
