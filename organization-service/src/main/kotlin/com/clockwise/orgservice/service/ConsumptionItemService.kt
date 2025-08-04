package com.clockwise.orgservice.service

import com.clockwise.orgservice.domain.ConsumptionItem
import com.clockwise.orgservice.domain.dto.ConsumptionItemDto
import com.clockwise.orgservice.domain.dto.CreateConsumptionItemDto
import com.clockwise.orgservice.domain.dto.UpdateConsumptionItemDto
import com.clockwise.orgservice.repositories.BusinessUnitRepository
import com.clockwise.orgservice.repositories.ConsumptionItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class ConsumptionItemService(
    private val consumptionItemRepository: ConsumptionItemRepository,
    private val businessUnitRepository: BusinessUnitRepository
) {
    
    /**
     * Get all consumption items for a business unit
     */
    fun getConsumptionItemsByBusinessUnit(businessUnitId: String): Flow<ConsumptionItemDto> {
        logger.info { "Getting consumption items for business unit: $businessUnitId" }
        return consumptionItemRepository.findByBusinessUnitId(businessUnitId)
            .map { it.toDto() }
    }
    
    /**
     * Create a new consumption item
     */
    suspend fun createConsumptionItem(
        businessUnitId: String, 
        createDto: CreateConsumptionItemDto
    ): ConsumptionItemDto {
        logger.info { "Creating consumption item for business unit: $businessUnitId" }
        
        // Basic validation
        if (createDto.name.isBlank()) {
            throw IllegalArgumentException("Name cannot be blank")
        }
        if (createDto.type.isBlank()) {
            throw IllegalArgumentException("Type cannot be blank")
        }
        if (createDto.price < java.math.BigDecimal.ZERO) {
            throw IllegalArgumentException("Price cannot be negative")
        }
        
        // Verify business unit exists
        businessUnitRepository.findById(businessUnitId)
            ?: throw IllegalArgumentException("Business unit not found with ID: $businessUnitId")
        
        val consumptionItem = ConsumptionItem(
            id = null, // Let the database generate the ID
            name = createDto.name.trim(),
            price = createDto.price,
            type = createDto.type.trim(),
            businessUnitId = businessUnitId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedItem = consumptionItemRepository.save(consumptionItem)
        logger.info { "Created consumption item with ID: ${savedItem.id}" }
        
        return savedItem.toDto()
    }
    
    /**
     * Update an existing consumption item
     */
    suspend fun updateConsumptionItem(
        businessUnitId: String,
        itemId: String,
        updateDto: UpdateConsumptionItemDto
    ): ConsumptionItemDto {
        logger.info { "Updating consumption item: $itemId for business unit: $businessUnitId" }
        
        // Basic validation for non-null fields
        if (updateDto.name?.isBlank() == true) {
            throw IllegalArgumentException("Name cannot be blank")
        }
        if (updateDto.type?.isBlank() == true) {
            throw IllegalArgumentException("Type cannot be blank")
        }
        if (updateDto.price != null && updateDto.price < java.math.BigDecimal.ZERO) {
            throw IllegalArgumentException("Price cannot be negative")
        }
        
        val existingItem = consumptionItemRepository.findById(itemId)
            ?: throw IllegalArgumentException("Consumption item not found with ID: $itemId")
        
        if (existingItem.businessUnitId != businessUnitId) {
            throw IllegalArgumentException("Consumption item does not belong to the specified business unit")
        }
        
        val updatedItem = existingItem.copy(
            name = updateDto.name?.trim() ?: existingItem.name,
            price = updateDto.price ?: existingItem.price,
            type = updateDto.type?.trim() ?: existingItem.type,
            updatedAt = LocalDateTime.now()
        )
        
        val savedItem = consumptionItemRepository.save(updatedItem)
        logger.info { "Updated consumption item with ID: ${savedItem.id}" }
        
        return savedItem.toDto()
    }
    
    /**
     * Delete a consumption item
     */
    suspend fun deleteConsumptionItem(businessUnitId: String, itemId: String) {
        logger.info { "Deleting consumption item: $itemId for business unit: $businessUnitId" }
        
        val exists = consumptionItemRepository.existsByIdAndBusinessUnitId(itemId, businessUnitId)
        if (!exists) {
            throw IllegalArgumentException("Consumption item not found or does not belong to the specified business unit")
        }
        
        consumptionItemRepository.deleteByIdAndBusinessUnitId(itemId, businessUnitId)
        logger.info { "Deleted consumption item with ID: $itemId" }
    }
    
    /**
     * Get consumption items by business unit and type
     */
    fun getConsumptionItemsByBusinessUnitAndType(
        businessUnitId: String, 
        type: String
    ): Flow<ConsumptionItemDto> {
        logger.info { "Getting consumption items for business unit: $businessUnitId, type: $type" }
        return consumptionItemRepository.findByBusinessUnitIdAndType(businessUnitId, type)
            .map { it.toDto() }
    }
}

/**
 * Extension function to convert ConsumptionItem entity to DTO
 */
private fun ConsumptionItem.toDto(): ConsumptionItemDto {
    return ConsumptionItemDto(
        id = this.id!!,
        name = this.name,
        price = this.price,
        type = this.type,
        businessUnitId = this.businessUnitId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
