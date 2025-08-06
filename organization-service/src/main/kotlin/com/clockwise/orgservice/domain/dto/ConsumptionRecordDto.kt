package com.clockwise.orgservice.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class ConsumptionRecordDto(
    val id: String?,
    val consumptionItemId: String,
    val consumptionItemName: String?,
    val userId: String,
    val workSessionId: String,
    val quantity: BigDecimal,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val consumedAt: LocalDateTime,
    val userFirstName: String?,
    val userLastName: String?
)

data class CreateConsumptionRecordDto(
    val consumptionItemId: String,
    val workSessionId: String,
    val quantity: BigDecimal
)

data class BulkCreateConsumptionRecordDto(
    val workSessionId: String,
    val consumptions: List<ConsumptionItemUsageDto>
)

data class ConsumptionItemUsageDto(
    val consumptionItemId: String,
    val quantity: BigDecimal
)