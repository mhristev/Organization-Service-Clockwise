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
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/companies")
class CompanyController(
    private val companyService: CompanyService,
    private val businessUnitService: BusinessUnitService
) {
    @PostMapping
    @Transactional
    suspend fun createCompany(@RequestBody company: Company): ResponseEntity<CompanyDto> = coroutineScope {
        val newCompany = async {
            companyService.createCompany(company)
        }
        ResponseEntity(newCompany.await().toCompanyDto(), HttpStatus.CREATED)
    }

    @GetMapping
    suspend fun getAllCompanies(): ResponseEntity<Flow<CompanyDto>> = coroutineScope {
        val companies = async { companyService.getAllCompanies().map { it.toCompanyDto() } }
        ResponseEntity(companies.await(), HttpStatus.OK)
    }

    @GetMapping("/{id}")
    suspend fun getCompanyById(@PathVariable id: String): ResponseEntity<CompanyDto> = coroutineScope {
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
        @RequestBody company: Company
    ): ResponseEntity<CompanyDto> = coroutineScope {
        val updatedCompany = async { companyService.updateCompany(id, company) }
        ResponseEntity(updatedCompany.await().toCompanyDto(), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteCompany(@PathVariable id: String): ResponseEntity<Void> = coroutineScope {
        async { companyService.deleteCompany(id) }
        ResponseEntity(HttpStatus.NO_CONTENT)

    }

    @GetMapping("/{id}/business-units")
    suspend fun getCompanyBusinessUnits(@PathVariable id: String): ResponseEntity<Flow<BusinessUnitDto>> = coroutineScope {
        ResponseEntity(businessUnitService.getBusinessUnitsByCompanyId(id).map { it.toBusinessUnitDto() }, HttpStatus.OK)
    }
}