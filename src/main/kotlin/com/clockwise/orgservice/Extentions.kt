package com.clockwise.orgservice

import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.domain.dto.CompanyDto

fun Company.toCompanyDto() = CompanyDto(
    id = this.id,
    name = this.name,
    description = this.description
)
