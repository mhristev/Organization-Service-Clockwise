package com.clockwise.orgservice.controller

import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.domain.dto.BusinessUnitDto
import com.clockwise.orgservice.domain.dto.CompanyDto
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.service.CompanyService
import com.clockwise.orgservice.toBusinessUnitDto
import com.clockwise.orgservice.toCompanyDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/v1/companies")
class CompanyController(
    private val companyService: CompanyService,
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
    
    @PostMapping
    suspend fun createCompany(
        @RequestBody company: Company,
        authentication: Authentication
    ): ResponseEntity<CompanyDto> {
        logger.info("User ${authentication.name} requested to create company: ${company.name}")
        val createdCompany = companyService.createCompany(company)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany.toCompanyDto())
    }

    @GetMapping
    suspend fun getAllCompanies(authentication: Authentication): ResponseEntity<Flow<CompanyDto>> = coroutineScope {
        // All authenticated users can view companies, but log access
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested to get all companies" }
        
        val companies = async { companyService.getAllCompanies().map { it.toCompanyDto() } }
        ResponseEntity(companies.await(), HttpStatus.OK)
    }

    @GetMapping("/{id}")
    suspend fun getCompanyById(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<CompanyDto> = coroutineScope {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested company with ID: $id" }
        
        val company = async { companyService.getCompanyById(id) }
        val result = company.await()
        if (result != null) {
            ResponseEntity(result.toCompanyDto(), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/{id}")
    suspend fun updateCompany(
        @PathVariable id: String,
        @RequestBody company: Company,
        authentication: Authentication
    ): ResponseEntity<CompanyDto> {
        logger.info("User ${authentication.name} requested to update company: $id")
        val updatedCompany = companyService.updateCompany(id, company)
        return ResponseEntity.ok(updatedCompany.toCompanyDto())
    }

    @DeleteMapping("/{id}")
    suspend fun deleteCompany(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        logger.info("User ${authentication.name} requested to delete company: $id")
        companyService.deleteCompany(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/business-units")
    suspend fun getCompanyBusinessUnits(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Flow<BusinessUnitDto>> = coroutineScope {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requested business units for company ID: $id" }
        
        val businessUnits = async { 
            businessUnitService.getBusinessUnitsByCompanyId(id).map { it.toBusinessUnitDto() }
        }
        ResponseEntity(businessUnits.await(), HttpStatus.OK)
    }
}