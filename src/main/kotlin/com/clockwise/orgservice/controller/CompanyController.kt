package com.clockwise.orgservice.controller

import com.clockwise.orgservice.config.*
import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.service.CompanyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus

//
//@RestController
//@RequestMapping("/v1/companies")
//class CompanyController(
//    private val companyService: CompanyService
//) {
//
//@PostMapping
//suspend fun createCompany(@RequestBody company: Company): ResponseEntity<Any> {
//    companyService.createCompany(company)
//        .onSuccess {
//            return ResponseEntity(it, HttpStatus.CREATED)
//        }
//        .onError {
//            return it.toResponseEntity()
//        }
//
//}
//
////    @GetMapping("/{id}")
////    suspend fun getCompanyById(@PathVariable id: UUID): ResponseEntity<Company> {
////        val company = companyService.getCompanyById(id)
////        return if (company != null) {
////            ResponseEntity(company, HttpStatus.OK)
////        } else {
////            ResponseEntity(HttpStatus.NOT_FOUND)
////        }
////    }
////
////    @PutMapping("/{id}")
////    suspend fun updateCompany(
////        @PathVariable id: UUID,
////        @RequestBody company: Company
////    ): ResponseEntity<Company> {
////        val updatedCompany = companyService.updateCompany(id, company)
////        return if (updatedCompany != null) {
////            ResponseEntity(updatedCompany, HttpStatus.OK)
////        } else {
////            ResponseEntity(HttpStatus.NOT_FOUND)
////        }
////    }
////
////    @DeleteMapping("/{id}")
////    suspend fun deleteCompany(@PathVariable id: UUID): ResponseEntity<Void> {
////        val deleted = companyService.deleteCompany(id)
////        return if (deleted) {
////            ResponseEntity(HttpStatus.NO_CONTENT)
////        } else {
////            ResponseEntity(HttpStatus.NOT_FOUND)
////        }
////    }
////
////    @GetMapping("/{id}/restaurants")
////    fun getCompanyRestaurants(@PathVariable id: UUID): ResponseEntity<Flow<Restaurant>> {
////        // First check if company exists
////        return ResponseEntity(restaurantService.getRestaurantsByCompanyId(id), HttpStatus.OK)
////    }
//}