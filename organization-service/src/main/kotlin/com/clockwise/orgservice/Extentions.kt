package com.clockwise.orgservice

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.domain.dto.BusinessUnitDto
import com.clockwise.orgservice.domain.dto.CompanyDto

fun Company.toCompanyDto() = CompanyDto(
    id = this.id,
    name = this.name,
    description = this.description
)


fun BusinessUnit.toBusinessUnitDto() = BusinessUnitDto(
    id = this.id,
    name = this.name,
    location = this.location,
    description = this.description,
)