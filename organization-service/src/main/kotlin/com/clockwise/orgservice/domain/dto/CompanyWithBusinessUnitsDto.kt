package com.clockwise.orgservice.domain.dto

data class CompanyWithBusinessUnitsDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val businessUnits: List<BusinessUnitDto>
) 