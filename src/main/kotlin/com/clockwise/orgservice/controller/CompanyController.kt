package com.clockwise.orgservice.controller

import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.domain.dto.CompanyDto
import com.clockwise.orgservice.service.CompanyService
import com.clockwise.orgservice.toCompanyDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/v1/companies")
class CompanyController(
    private val companyService: CompanyService
) {
    @PostMapping
    @Transactional
    suspend fun createCompany(@RequestBody company: Company): ResponseEntity<CompanyDto> = coroutineScope {
        val newCompany = async {
            companyService.createCompany(company)
        }
        ResponseEntity(newCompany.await().toCompanyDto(), HttpStatus.CREATED)
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

//    @GetMapping("/{id}/restaurants")
//    fun getCompanyRestaurants(@PathVariable id: UUID): ResponseEntity<Flow<Restaurant>> {
//        // First check if company exists
//        return ResponseEntity(restaurantService.getRestaurantsByCompanyId(id), HttpStatus.OK)
//    }
}