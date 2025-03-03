package com.clockwise.orgservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("companies")
data class Company(
    @Id val id: String? = null,
    val name: String,
    val description: String
)
