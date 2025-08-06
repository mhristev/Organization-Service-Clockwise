package com.clockwise.orgservice.repositories

import com.clockwise.orgservice.domain.ConsumptionRecord
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ConsumptionRecordRepository : CoroutineCrudRepository<ConsumptionRecord, String> {
    
    @Query("""
        SELECT cr.* FROM consumption_records cr
        JOIN consumption_items ci ON cr.consumption_item_id = ci.id
        WHERE ci.business_unit_id = :businessUnitId
        ORDER BY cr.consumed_at DESC
    """)
    fun findByBusinessUnitId(businessUnitId: String): Flow<ConsumptionRecord>
    
    @Query("""
        SELECT cr.* FROM consumption_records cr
        JOIN consumption_items ci ON cr.consumption_item_id = ci.id
        WHERE ci.business_unit_id = :businessUnitId
        AND cr.consumed_at BETWEEN :startDate AND :endDate
        ORDER BY cr.consumed_at DESC
    """)
    fun findByBusinessUnitIdAndDateRange(
        businessUnitId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<ConsumptionRecord>
    
    fun findByWorkSessionId(workSessionId: String): Flow<ConsumptionRecord>
    
    fun findByUserId(userId: String): Flow<ConsumptionRecord>
    
    @Query("""
        SELECT cr.* FROM consumption_records cr
        WHERE cr.user_id = :userId
        AND cr.consumed_at BETWEEN :startDate AND :endDate
        ORDER BY cr.consumed_at DESC
    """)
    fun findByUserIdAndDateRange(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<ConsumptionRecord>
}