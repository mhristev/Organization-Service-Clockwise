package com.clockwise.orgservice

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.domain.CompanyWithBusinessUnits
import com.clockwise.orgservice.domain.dto.BusinessUnitDto
import com.clockwise.orgservice.domain.dto.BusinessUnitAddressDto
import com.clockwise.orgservice.domain.dto.CompanyDto
import com.clockwise.orgservice.domain.dto.CompanyWithBusinessUnitsDto

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
    companyId = this.companyId,
    latitude = this.latitude,
    longitude = this.longitude,
    allowedRadius = this.allowedRadius
)

fun BusinessUnit.toBusinessUnitAddressDto() = BusinessUnitAddressDto(
    id = this.id ?: "",
    name = this.name,
    address = this.location,
    latitude = this.latitude ?: 0.0,
    longitude = this.longitude ?: 0.0,
    allowedRadius = this.allowedRadius
)

fun CompanyWithBusinessUnits.toCompanyWithBusinessUnitsDto() = CompanyWithBusinessUnitsDto(
    id = this.id,
    name = this.name,
    description = this.description,
    businessUnits = this.businessUnits.map { it.toBusinessUnitDto() }
)