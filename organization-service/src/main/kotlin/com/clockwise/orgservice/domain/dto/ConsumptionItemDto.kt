package com.clockwise.orgservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * DTO for consumption item responses
 */
data class ConsumptionItemDto(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("price")
    val price: BigDecimal,
    
    @JsonProperty("type")
    val type: String,
    
    @JsonProperty("business_unit_id")
    val businessUnitId: String,
    
    @JsonProperty("created_at")
    val createdAt: LocalDateTime,
    
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime
)

/**
 * DTO for creating new consumption items
 */
data class CreateConsumptionItemDto(
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("price")
    val price: BigDecimal,
    
    @JsonProperty("type")
    val type: String
)

/**
 * DTO for updating consumption items (all fields optional)
 */
data class UpdateConsumptionItemDto(
    @JsonProperty("name")
    val name: String?,
    
    @JsonProperty("price")
    val price: BigDecimal?,
    
    @JsonProperty("type")
    val type: String?
)
