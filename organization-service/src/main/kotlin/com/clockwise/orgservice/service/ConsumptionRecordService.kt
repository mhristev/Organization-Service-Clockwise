package com.clockwise.orgservice.service

import com.clockwise.orgservice.domain.ConsumptionRecord
import com.clockwise.orgservice.domain.dto.BulkCreateConsumptionRecordDto
import com.clockwise.orgservice.domain.dto.ConsumptionRecordDto
import com.clockwise.orgservice.domain.dto.CreateConsumptionRecordDto
import com.clockwise.orgservice.repositories.ConsumptionRecordRepository
import com.clockwise.orgservice.repositories.ConsumptionItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class ConsumptionRecordService(
    private val consumptionRecordRepository: ConsumptionRecordRepository,
    private val consumptionItemRepository: ConsumptionItemRepository
) {
    
    suspend fun recordConsumption(userId: String, createDto: CreateConsumptionRecordDto): ConsumptionRecordDto {
        logger.info { "Recording consumption for user: $userId, work session: ${createDto.workSessionId}" }
        
        // Validate consumption item exists
        val consumptionItem = consumptionItemRepository.findById(createDto.consumptionItemId)
            ?: throw IllegalArgumentException("Consumption item not found with ID: ${createDto.consumptionItemId}")
        
        val record = ConsumptionRecord(
            consumptionItemId = createDto.consumptionItemId,
            userId = userId,
            workSessionId = createDto.workSessionId,
            quantity = createDto.quantity,
            consumedAt = LocalDateTime.now()
        )
        
        val savedRecord = consumptionRecordRepository.save(record)
        logger.info { "Created consumption record with ID: ${savedRecord.id}" }
        
        return ConsumptionRecordDto(
            id = savedRecord.id,
            consumptionItemId = savedRecord.consumptionItemId,
            consumptionItemName = consumptionItem.name,
            userId = savedRecord.userId,
            workSessionId = savedRecord.workSessionId,
            quantity = savedRecord.quantity,
            consumedAt = savedRecord.consumedAt
        )
    }
    
    suspend fun recordBulkConsumption(userId: String, bulkDto: BulkCreateConsumptionRecordDto): List<ConsumptionRecordDto> {
        logger.info { "Recording bulk consumption for user: $userId, work session: ${bulkDto.workSessionId}" }
        
        val results = mutableListOf<ConsumptionRecordDto>()
        
        for (consumption in bulkDto.consumptions) {
            val createDto = CreateConsumptionRecordDto(
                consumptionItemId = consumption.consumptionItemId,
                workSessionId = bulkDto.workSessionId,
                quantity = consumption.quantity
            )
            
            try {
                val recordDto = recordConsumption(userId, createDto)
                results.add(recordDto)
            } catch (e: Exception) {
                logger.error { "Failed to record consumption for item ${consumption.consumptionItemId}: ${e.message}" }
                throw e
            }
        }
        
        return results
    }
    
    fun getConsumptionsByBusinessUnit(businessUnitId: String): Flow<ConsumptionRecordDto> {
        logger.info { "Getting consumption records for business unit: $businessUnitId" }
        return consumptionRecordRepository.findByBusinessUnitId(businessUnitId)
            .map { record -> createConsumptionRecordDto(record) }
    }
    
    fun getConsumptionsByBusinessUnitAndDateRange(
        businessUnitId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<ConsumptionRecordDto> {
        logger.info { "Getting consumption records for business unit: $businessUnitId from $startDate to $endDate" }
        return consumptionRecordRepository.findByBusinessUnitIdAndDateRange(businessUnitId, startDate, endDate)
            .map { record -> createConsumptionRecordDto(record) }
    }
    
    fun getConsumptionsByWorkSession(workSessionId: String): Flow<ConsumptionRecordDto> {
        logger.info { "Getting consumption records for work session: $workSessionId" }
        return consumptionRecordRepository.findByWorkSessionId(workSessionId)
            .map { record -> createConsumptionRecordDto(record) }
    }
    
    private suspend fun createConsumptionRecordDto(record: ConsumptionRecord): ConsumptionRecordDto {
        val consumptionItem = consumptionItemRepository.findById(record.consumptionItemId)
        
        return ConsumptionRecordDto(
            id = record.id,
            consumptionItemId = record.consumptionItemId,
            consumptionItemName = consumptionItem?.name,
            userId = record.userId,
            workSessionId = record.workSessionId,
            quantity = record.quantity,
            consumedAt = record.consumedAt
        )
    }
}