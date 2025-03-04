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
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/business-units")
class BusinessUnitController(private val service: BusinessUnitService) {

    // Create a new business unit
    @PostMapping
    suspend fun createBusinessUnit(@RequestBody businessUnit: BusinessUnit): ResponseEntity<BusinessUnitDto> = coroutineScope {
       val newUnit = async { service.createBusinessUnit(businessUnit)}
        ResponseEntity(newUnit.await().toBusinessUnitDto(), HttpStatus.CREATED)
    }

    // Update an existing business unit
    @PutMapping("/{id}")
    suspend fun updateBusinessUnit(
        @PathVariable id: String,
        @RequestBody businessUnit: BusinessUnit
    ): ResponseEntity<BusinessUnitDto> = coroutineScope {
        val updatedUnit = async { service.updateBusinessUnit(businessUnit) }
        ResponseEntity(updatedUnit.await().toBusinessUnitDto(), HttpStatus.OK)
    }

    // Delete a business unit
    @DeleteMapping("/{id}")
    suspend fun deleteBusinessUnit(@PathVariable id: String): ResponseEntity<Void> = coroutineScope {
        async { service.deleteBusinessUnit(id) }
        ResponseEntity(HttpStatus.NO_CONTENT)
    }

    // Get a specific business unit
    @GetMapping("/{id}")
    suspend fun getBusinessUnit(@PathVariable id: String): ResponseEntity<BusinessUnitDto> = coroutineScope {
        val unit = async { service.getBusinessUnitById(id) }
        val found = unit.await()
        if (found != null) {
            ResponseEntity(found.toBusinessUnitDto(), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}