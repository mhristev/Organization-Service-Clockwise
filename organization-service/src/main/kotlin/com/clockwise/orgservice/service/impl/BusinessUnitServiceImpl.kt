package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.service.KafkaService
import org.springframework.stereotype.Service
import com.clockwise.orgservice.repositories.BusinessUnitRepository
import kotlinx.coroutines.flow.Flow

@Service
class BusinessUnitServiceImpl(
    private val repository: BusinessUnitRepository,
    private val kafkaService: KafkaService
) : BusinessUnitService {

    override suspend fun createBusinessUnit(businessUnit: BusinessUnit): BusinessUnit {
        val savedBusinessUnit = repository.save(businessUnit)
        kafkaService.sendBusinessUnitCreatedMessage(savedBusinessUnit.id!!, savedBusinessUnit.name)
        return savedBusinessUnit
    }

    override suspend fun updateBusinessUnit(businessUnit: BusinessUnit): BusinessUnit {
        val updatedBusinessUnit = repository.save(businessUnit)
        kafkaService.sendBusinessUnitUpdatedMessage(updatedBusinessUnit.id!!, updatedBusinessUnit.name)
        return updatedBusinessUnit
    }

    override suspend fun deleteBusinessUnit(id: String) {
        val businessUnit = repository.findById(id)
        if (businessUnit != null) {
            repository.deleteById(id)
            kafkaService.sendBusinessUnitDeletedMessage(businessUnit.id!!, businessUnit.name)
        }
    }

    override suspend fun getBusinessUnitById(id: String): BusinessUnit? {
        return repository.findById(id)
    }

    override fun getBusinessUnitsByCompanyId(companyId: String): Flow<BusinessUnit> {
        return repository.findByCompanyId(companyId)
    }

}