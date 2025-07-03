package com.clockwise.orgservice.domain

data class CompanyWithBusinessUnits(
    val id: String? = null,
    val name: String,
    val description: String,
    val businessUnits: List<BusinessUnit>
) 