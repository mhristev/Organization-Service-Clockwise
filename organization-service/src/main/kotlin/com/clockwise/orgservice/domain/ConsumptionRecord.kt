package com.clockwise.orgservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("consumption_records")
data class ConsumptionRecord(
    @Id val id: String? = null,
    @Column("consumption_item_id") val consumptionItemId: String,
    @Column("user_id") val userId: String,
    @Column("work_session_id") val workSessionId: String,
    val quantity: BigDecimal,
    @Column("consumed_at") val consumedAt: LocalDateTime = LocalDateTime.now()
)