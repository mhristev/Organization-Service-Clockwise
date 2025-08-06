package com.clockwise.orgservice.controller

import com.clockwise.orgservice.domain.dto.BulkCreateConsumptionRecordDto
import com.clockwise.orgservice.domain.dto.ConsumptionRecordDto
import com.clockwise.orgservice.domain.dto.CreateConsumptionRecordDto
import com.clockwise.orgservice.service.ConsumptionRecordService
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/v1/consumption-records")
@CrossOrigin(origins = ["*"])
class ConsumptionRecordController(
    private val consumptionRecordService: ConsumptionRecordService
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
    suspend fun recordConsumption(
        @RequestBody createDto: CreateConsumptionRecordDto,
        authentication: Authentication
    ): ResponseEntity<ConsumptionRecordDto> {
        val userInfo = extractUserInfo(authentication)
        val userId = userInfo["userId"] as String
        
        logger.info { "User ${userInfo["email"]} recording consumption for work session: ${createDto.workSessionId}" }
        
        return try {
            val consumptionRecord = consumptionRecordService.recordConsumption(userId, createDto)
            ResponseEntity.status(HttpStatus.CREATED).body(consumptionRecord)
        } catch (e: IllegalArgumentException) {
            logger.error { "Error recording consumption: ${e.message}" }
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error recording consumption" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    @PostMapping("/bulk")
    suspend fun recordBulkConsumption(
        @RequestBody bulkDto: BulkCreateConsumptionRecordDto,
        authentication: Authentication
    ): ResponseEntity<List<ConsumptionRecordDto>> {
        val userInfo = extractUserInfo(authentication)
        val userId = userInfo["userId"] as String
        
        logger.info { "User ${userInfo["email"]} recording bulk consumption for work session: ${bulkDto.workSessionId}" }
        
        return try {
            val consumptionRecords = consumptionRecordService.recordBulkConsumption(userId, bulkDto)
            ResponseEntity.status(HttpStatus.CREATED).body(consumptionRecords)
        } catch (e: IllegalArgumentException) {
            logger.error { "Error recording bulk consumption: ${e.message}" }
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error recording bulk consumption" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    @GetMapping("/business-unit/{businessUnitId}")
    fun getConsumptionsByBusinessUnit(
        @PathVariable businessUnitId: String,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        authentication: Authentication
    ): ResponseEntity<Flow<ConsumptionRecordDto>> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requesting consumption records for business unit: $businessUnitId" }
        
        val records = if (startDate != null && endDate != null) {
            consumptionRecordService.getConsumptionsByBusinessUnitAndDateRange(businessUnitId, startDate, endDate)
        } else {
            consumptionRecordService.getConsumptionsByBusinessUnit(businessUnitId)
        }
        
        return ResponseEntity.ok(records)
    }
    
    @GetMapping("/work-session/{workSessionId}")
    fun getConsumptionsByWorkSession(
        @PathVariable workSessionId: String,
        authentication: Authentication
    ): ResponseEntity<Flow<ConsumptionRecordDto>> {
        val userInfo = extractUserInfo(authentication)
        logger.info { "User ${userInfo["email"]} requesting consumption records for work session: $workSessionId" }
        
        val records = consumptionRecordService.getConsumptionsByWorkSession(workSessionId)
        return ResponseEntity.ok(records)
    }
}