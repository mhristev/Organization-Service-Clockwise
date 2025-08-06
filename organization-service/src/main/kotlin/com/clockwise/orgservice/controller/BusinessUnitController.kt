package com.clockwise.orgservice.controller

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.dto.BusinessUnitDto
import com.clockwise.orgservice.domain.dto.BusinessUnitAddressDto
import com.clockwise.orgservice.domain.dto.BusinessUnitPartialUpdateDto
import com.clockwise.orgservice.domain.dto.BusinessUnitFullUpdateDto
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.toBusinessUnitDto
import com.clockwise.orgservice.toBusinessUnitAddressDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/v1/business-units")
class BusinessUnitController(
    private val businessUnitService: BusinessUnitService
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

    @GetMapping
    suspend fun getAllBusinessUnits(
        authentication: Authentication
    ): ResponseEntity<Flow<BusinessUnitDto>> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested all business units" }
        
        val businessUnits = businessUnitService.getAllBusinessUnits().map { it.toBusinessUnitDto() }
        return ResponseEntity.ok(businessUnits)
    }

    @PostMapping
    suspend fun createBusinessUnit(
        @RequestBody businessUnit: BusinessUnit,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> {
        logger.info("User ${authentication.name} requested to create business unit: ${businessUnit.name}")
        val createdBusinessUnit = businessUnitService.createBusinessUnit(businessUnit)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBusinessUnit.toBusinessUnitDto())
    }

    @GetMapping("/{id}")
    suspend fun getBusinessUnit(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested business unit with ID: $id" }
        
        val businessUnit = businessUnitService.getBusinessUnitById(id)
        return if (businessUnit != null) {
            ResponseEntity.ok(businessUnit.toBusinessUnitDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    suspend fun updateBusinessUnit(
        @PathVariable id: String,
        @RequestBody businessUnit: BusinessUnit,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> {
        logger.info("User ${authentication.name} requested to update business unit: $id")
        // Update the business unit with the provided ID
        val businessUnitWithId = businessUnit.copy(id = id)
        val updatedBusinessUnit = businessUnitService.updateBusinessUnit(businessUnitWithId)
        return ResponseEntity.ok(updatedBusinessUnit.toBusinessUnitDto())
    }

    @DeleteMapping("/{id}")
    suspend fun deleteBusinessUnit(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        logger.info("User ${authentication.name} requested to delete business unit: $id")
        businessUnitService.deleteBusinessUnit(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/address")
    suspend fun getBusinessUnitAddress(
        @PathVariable id: String,
        authentication: Authentication?
    ): ResponseEntity<BusinessUnitAddressDto> {
        if (authentication != null) {
            val userInfo = extractUserInfo(authentication)
            logger.info { "User ${userInfo["email"]} requested address for business unit with ID: $id" }
        } else {
            logger.info { "Anonymous request for business unit address with ID: $id" }
        }
        
        val businessUnit = businessUnitService.getBusinessUnitById(id)
        return if (businessUnit != null) {
            // Validate that the business unit has location data
            if (businessUnit.latitude != null && businessUnit.longitude != null) {
                ResponseEntity.ok(businessUnit.toBusinessUnitAddressDto())
            } else {
                logger.warn { "Business unit $id exists but has no location data" }
                ResponseEntity.notFound().build()
            }
        } else {
            logger.warn { "Business unit with ID $id not found" }
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{id}/details")
    suspend fun updateBusinessUnitDetails(
        @PathVariable id: String,
        @RequestBody updateDto: BusinessUnitPartialUpdateDto,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> {
        logger.info("User ${authentication.name} requested to update details for business unit: $id")
        return try {
            val updatedBusinessUnit = businessUnitService.updateBusinessUnitPartial(id, updateDto)
            ResponseEntity.ok(updatedBusinessUnit.toBusinessUnitDto())
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}/complete")
    suspend fun updateBusinessUnitComplete(
        @PathVariable id: String,
        @RequestBody updateDto: BusinessUnitFullUpdateDto,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> {
        logger.info("User ${authentication.name} requested to completely update business unit: $id")
        return try {
            val updatedBusinessUnit = businessUnitService.updateBusinessUnitFull(id, updateDto)
            ResponseEntity.ok(updatedBusinessUnit.toBusinessUnitDto())
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}