package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.service.BusinessUnitService
import org.springframework.stereotype.Service
import com.clockwise.orgservice.repositories.BusinessUnitRepository
import kotlinx.coroutines.flow.Flow

@Service
class BusinessUnitServiceImpl(private val repository: BusinessUnitRepository) : BusinessUnitService {

    override suspend fun createBusinessUnit(businessUnit: BusinessUnit): BusinessUnit {
        return repository.save(businessUnit)
    }

    override suspend fun updateBusinessUnit(businessUnit: BusinessUnit): BusinessUnit {
        return repository.save(businessUnit)
    }

    override suspend fun deleteBusinessUnit(id: String) {
        repository.deleteById(id)
    }

    override suspend fun getBusinessUnitById(id: String): BusinessUnit? {
        return repository.findById(id)
    }

    override fun getBusinessUnitsByCompanyId(companyId: String): Flow<BusinessUnit> {
        return repository.findByCompanyId(companyId)
    }

}