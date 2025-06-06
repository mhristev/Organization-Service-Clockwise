package com.clockwise.orgservice.controller

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.dto.BusinessUnitDto
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.toBusinessUnitDto
import com.clockwise.orgservice.toCompanyDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/v1/business-units")
class BusinessUnitController(private val service: BusinessUnitService) {

    private fun extractUserInfo(authentication: Authentication): Map<String, Any?> {
        val jwt = authentication.principal as Jwt
        return mapOf(
            "userId" to jwt.getClaimAsString("sub"),
            "email" to jwt.getClaimAsString("email"),
            "firstName" to jwt.getClaimAsString("given_name"),
            "lastName" to jwt.getClaimAsString("family_name"),
            "roles" to jwt.getClaimAsStringList("roles")
        )
    }

    // Create a new business unit
    @PostMapping
    suspend fun createBusinessUnit(
        @RequestBody businessUnit: BusinessUnit,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> = coroutineScope {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested to create business unit: ${businessUnit.name}" }
        
        val newUnit = async { service.createBusinessUnit(businessUnit)}
        ResponseEntity(newUnit.await().toBusinessUnitDto(), HttpStatus.CREATED)
    }

    // Update an existing business unit
    @PutMapping("/{id}")
    suspend fun updateBusinessUnit(
        @PathVariable id: String,
        @RequestBody businessUnit: BusinessUnit,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> = coroutineScope {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested to update business unit with ID: $id" }
        
        val updatedUnit = async { service.updateBusinessUnit(businessUnit) }
        ResponseEntity(updatedUnit.await().toBusinessUnitDto(), HttpStatus.OK)
    }

    // Delete a business unit
    @DeleteMapping("/{id}")
    suspend fun deleteBusinessUnit(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Void> = coroutineScope {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested to delete business unit with ID: $id" }
        
        async { service.deleteBusinessUnit(id) }
        ResponseEntity(HttpStatus.NO_CONTENT)
    }

    // Get a specific business unit
    @GetMapping("/{id}")
    suspend fun getBusinessUnit(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<BusinessUnitDto> = coroutineScope {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested to get business unit with ID: $id" }
        
        val unit = async { service.getBusinessUnitById(id) }
        val found = unit.await()
        if (found != null) {
            ResponseEntity(found.toBusinessUnitDto(), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}