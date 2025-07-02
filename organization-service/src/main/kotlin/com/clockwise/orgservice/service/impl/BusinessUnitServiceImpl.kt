package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.service.KafkaService
import org.springframework.stereotype.Service
import com.clockwise.orgservice.repositories.BusinessUnitRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Service
class BusinessUnitServiceImpl(
    private val repository: BusinessUnitRepository,
    private val kafkaService: KafkaService
) : BusinessUnitService {

    override suspend fun createBusinessUnit(businessUnit: BusinessUnit): BusinessUnit {
        logger.debug { "Creating business unit: $businessUnit" }
        val savedBusinessUnit = repository.save(businessUnit)
        logger.info { "Created business unit with ID: ${savedBusinessUnit.id}" }
        kafkaService.sendBusinessUnitCreatedMessage(savedBusinessUnit.id!!, savedBusinessUnit.name)
        return savedBusinessUnit
    }

    override suspend fun updateBusinessUnit(businessUnit: BusinessUnit): BusinessUnit {
        logger.debug { "Updating business unit with ID: ${businessUnit.id}" }
        val updatedBusinessUnit = repository.save(businessUnit)
        logger.info { "Updated business unit with ID: ${updatedBusinessUnit.id}" }
        kafkaService.sendBusinessUnitUpdatedMessage(updatedBusinessUnit.id!!, updatedBusinessUnit.name)
        return updatedBusinessUnit
    }

    override suspend fun deleteBusinessUnit(id: String) {
        logger.debug { "Deleting business unit with ID: $id" }
        val businessUnit = repository.findById(id)
        if (businessUnit != null) {
            repository.deleteById(id)
            logger.info { "Deleted business unit with ID: $id" }
            kafkaService.sendBusinessUnitDeletedMessage(businessUnit.id!!, businessUnit.name)
        } else {
            logger.warn { "Attempted to delete non-existent business unit with ID: $id" }
        }
    }

    override suspend fun getBusinessUnitById(id: String): BusinessUnit? {
        logger.debug { "Fetching business unit with ID: $id" }
        val businessUnit = repository.findById(id)
        if (businessUnit == null) {
            logger.debug { "No business unit found with ID: $id" }
        }
        return businessUnit
    }

    override fun getBusinessUnitsByCompanyId(companyId: String): Flow<BusinessUnit> {
        logger.debug { "Fetching business units for company ID: $companyId" }
        return repository.findByCompanyId(companyId)
    }

    override fun getAllBusinessUnits(): Flow<BusinessUnit> {
        logger.debug { "Fetching all business units" }
        return repository.findAll()
    }

}