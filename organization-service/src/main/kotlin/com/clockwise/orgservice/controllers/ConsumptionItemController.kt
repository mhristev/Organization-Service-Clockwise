package com.clockwise.orgservice.controllers

import com.clockwise.orgservice.domain.dto.ConsumptionItemDto
import com.clockwise.orgservice.domain.dto.CreateConsumptionItemDto
import com.clockwise.orgservice.domain.dto.UpdateConsumptionItemDto
import com.clockwise.orgservice.service.ConsumptionItemService
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/v1/business-units")
@CrossOrigin(origins = ["*"])
class ConsumptionItemController(
    private val consumptionItemService: ConsumptionItemService
) {
    
    private fun extractUserInfo(authentication: Authentication): Map<String, Any?> {
        val jwt = authentication.principal as Jwt
        return mapOf(
            "userId" to jwt.getClaimAsString("sub"),
            "email" to jwt.getClaimAsString("email"),
            "firstName" to jwt.getClaimAsString("given_name"),
            "lastName" to jwt.getClaimAsString("family_name")
        )
    }
    
    /**
     * GET /v1/business-units/{id}/consumption-items - List items
     */
    @GetMapping("/{id}/consumption-items")
    suspend fun getConsumptionItems(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Flow<ConsumptionItemDto>> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested consumption items for business unit: $id" }
        
        val items = consumptionItemService.getConsumptionItemsByBusinessUnit(id)
        return ResponseEntity.ok(items)
    }
    
    /**
     * GET /v1/business-units/{id}/consumption-items?type={type} - List items by type
     */
    @GetMapping("/{id}/consumption-items", params = ["type"])
    suspend fun getConsumptionItemsByType(
        @PathVariable id: String,
        @RequestParam type: String,
        authentication: Authentication
    ): ResponseEntity<Flow<ConsumptionItemDto>> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested consumption items for business unit: $id, type: $type" }
        
        val items = consumptionItemService.getConsumptionItemsByBusinessUnitAndType(id, type)
        return ResponseEntity.ok(items)
    }
    
    /**
     * POST /v1/business-units/{id}/consumption-items - Create item
     */
    @PostMapping("/{id}/consumption-items")
    suspend fun createConsumptionItem(
        @PathVariable id: String,
        @RequestBody createDto: CreateConsumptionItemDto,
        authentication: Authentication
    ): ResponseEntity<ConsumptionItemDto> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} creating consumption item for business unit: $id" }
        
        return try {
            // Basic validation
            if (createDto.name.isBlank()) {
                logger.warn { "Validation failed: Name cannot be blank" }
                return ResponseEntity.badRequest().build()
            }
            if (createDto.type.isBlank()) {
                logger.warn { "Validation failed: Type cannot be blank" }
                return ResponseEntity.badRequest().build()
            }
            if (createDto.price < java.math.BigDecimal.ZERO) {
                logger.warn { "Validation failed: Price cannot be negative" }
                return ResponseEntity.badRequest().build()
            }
            
            val createdItem = consumptionItemService.createConsumptionItem(id, createDto)
            ResponseEntity.status(HttpStatus.CREATED).body(createdItem)
        } catch (e: IllegalArgumentException) {
            logger.error { "Error creating consumption item: ${e.message}" }
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error creating consumption item" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    /**
     * PUT /v1/business-units/{id}/consumption-items/{itemId} - Update item
     */
    @PutMapping("/{id}/consumption-items/{itemId}")
    suspend fun updateConsumptionItem(
        @PathVariable id: String,
        @PathVariable itemId: String,
        @RequestBody updateDto: UpdateConsumptionItemDto,
        authentication: Authentication
    ): ResponseEntity<ConsumptionItemDto> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} updating consumption item: $itemId for business unit: $id" }
        
        return try {
            // Basic validation for non-null fields
            if (updateDto.name?.isBlank() == true) {
                logger.warn { "Validation failed: Name cannot be blank" }
                return ResponseEntity.badRequest().build()
            }
            if (updateDto.type?.isBlank() == true) {
                logger.warn { "Validation failed: Type cannot be blank" }
                return ResponseEntity.badRequest().build()
            }
            if (updateDto.price != null && updateDto.price < java.math.BigDecimal.ZERO) {
                logger.warn { "Validation failed: Price cannot be negative" }
                return ResponseEntity.badRequest().build()
            }
            
            val updatedItem = consumptionItemService.updateConsumptionItem(id, itemId, updateDto)
            ResponseEntity.ok(updatedItem)
        } catch (e: IllegalArgumentException) {
            logger.error { "Error updating consumption item: ${e.message}" }
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error updating consumption item" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    /**
     * DELETE /v1/business-units/{id}/consumption-items/{itemId} - Delete item
     */
    @DeleteMapping("/{id}/consumption-items/{itemId}")
    suspend fun deleteConsumptionItem(
        @PathVariable id: String,
        @PathVariable itemId: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} deleting consumption item: $itemId for business unit: $id" }
        
        return try {
            consumptionItemService.deleteConsumptionItem(id, itemId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error { "Error deleting consumption item: ${e.message}" }
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error deleting consumption item" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
