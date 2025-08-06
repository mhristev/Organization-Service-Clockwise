package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.dto.BusinessUnitPartialUpdateDto
import com.clockwise.orgservice.domain.dto.BusinessUnitFullUpdateDto
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.service.KafkaService
import com.clockwise.orgservice.service.GeocodingService
import org.springframework.stereotype.Service
import com.clockwise.orgservice.repositories.BusinessUnitRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Service
class BusinessUnitServiceImpl(
    private val repository: BusinessUnitRepository,
    private val kafkaService: KafkaService,
    private val geocodingService: GeocodingService
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

    override suspend fun updateBusinessUnitPartial(id: String, updateDto: BusinessUnitPartialUpdateDto): BusinessUnit {
        logger.debug { "Partially updating business unit with ID: $id" }
        val existing = repository.findById(id)
            ?: throw IllegalArgumentException("Business unit with ID $id not found")
        
        val updated = existing.copy(
            description = updateDto.description ?: existing.description,
            phoneNumber = updateDto.phoneNumber ?: existing.phoneNumber,
            email = updateDto.email ?: existing.email
        )
        
        val savedBusinessUnit = repository.save(updated)
        logger.info { "Partially updated business unit with ID: $id" }
        kafkaService.sendBusinessUnitUpdatedMessage(savedBusinessUnit.id!!, savedBusinessUnit.name)
        return savedBusinessUnit
    }

    override suspend fun updateBusinessUnitFull(id: String, updateDto: BusinessUnitFullUpdateDto): BusinessUnit {
        logger.debug { "Fully updating business unit with ID: $id" }
        val existing = repository.findById(id)
            ?: throw IllegalArgumentException("Business unit with ID $id not found")
        
        // Get coordinates if location changed and latitude/longitude not provided
        val coordinates = when {
            updateDto.latitude != null && updateDto.longitude != null -> {
                Pair(updateDto.latitude, updateDto.longitude)
            }
            updateDto.location != existing.location -> {
                try {
                    geocodingService.getCoordinates(updateDto.location)
                } catch (e: Exception) {
                    logger.warn { "Failed to get coordinates for location: ${updateDto.location}" }
                    Pair(existing.latitude, existing.longitude)
                }
            }
            else -> Pair(existing.latitude, existing.longitude)
        }
        
        val updated = existing.copy(
            name = updateDto.name,
            location = updateDto.location,
            description = updateDto.description,
            latitude = coordinates.first,
            longitude = coordinates.second,
            phoneNumber = updateDto.phoneNumber,
            email = updateDto.email
        )
        
        val savedBusinessUnit = repository.save(updated)
        logger.info { "Fully updated business unit with ID: $id" }
        kafkaService.sendBusinessUnitUpdatedMessage(savedBusinessUnit.id!!, savedBusinessUnit.name)
        return savedBusinessUnit
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