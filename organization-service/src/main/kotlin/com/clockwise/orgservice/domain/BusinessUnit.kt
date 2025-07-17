package com.clockwise.orgservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("business_unit")
data class BusinessUnit(
    @Id val id: String? = null,
    val name: String,
    val location: String,
    val description: String,
    @Column("company_id") val companyId: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @Column("allowed_radius") val allowedRadius: Double = 200.0 // Default 200 meters
)
