package com.clockwise.orgservice.domain

//import org.hibernate.validator.constraints.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


data class BusinessUnit(
    @Id val id: String? = null,
    val name: String,
    val location: String,
    val description: String,
    @Column("company_id") val companyId: Long
)