package com.clockwise.orgservice.service

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.dto.BusinessUnitPartialUpdateDto
import com.clockwise.orgservice.domain.dto.BusinessUnitFullUpdateDto
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
interface BusinessUnitService {
    suspend fun createBusinessUnit(businessUnit: BusinessUnit): BusinessUnit
    suspend fun updateBusinessUnit(businessUnit: BusinessUnit): BusinessUnit
    suspend fun updateBusinessUnitPartial(id: String, updateDto: BusinessUnitPartialUpdateDto): BusinessUnit
    suspend fun updateBusinessUnitFull(id: String, updateDto: BusinessUnitFullUpdateDto): BusinessUnit
    suspend fun deleteBusinessUnit(id: String)
    suspend fun getBusinessUnitById(id: String): BusinessUnit?
    fun getBusinessUnitsByCompanyId(companyId: String): Flow<BusinessUnit>
    fun getAllBusinessUnits(): Flow<BusinessUnit>
}