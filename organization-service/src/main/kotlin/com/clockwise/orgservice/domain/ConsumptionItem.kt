package com.clockwise.orgservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("consumption_items")
data class ConsumptionItem(
    @Id val id: String? = null,
    val name: String,
    val price: BigDecimal,
    val type: String,
    @Column("business_unit_id") val businessUnitId: String,
    @Column("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at") val updatedAt: LocalDateTime = LocalDateTime.now()
)
