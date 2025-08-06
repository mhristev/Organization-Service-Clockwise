package com.clockwise.orgservice.domain.dto

import org.springframework.data.annotation.Id

data class CompanyDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val phoneNumber: String? = null,
    val email: String? = null
)