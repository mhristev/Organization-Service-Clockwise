package com.clockwise.orgservice.repositories

import com.clockwise.orgservice.domain.ConsumptionItem
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConsumptionItemRepository : CoroutineCrudRepository<ConsumptionItem, String> {
    
    /**
     * Find all consumption items for a specific business unit
     */
    fun findByBusinessUnitId(businessUnitId: String): Flow<ConsumptionItem>
    
    /**
     * Find consumption items by business unit and type
     */
    fun findByBusinessUnitIdAndType(businessUnitId: String, type: String): Flow<ConsumptionItem>
    
    /**
     * Check if a consumption item exists with the given id and business unit id
     */
    suspend fun existsByIdAndBusinessUnitId(id: String, businessUnitId: String): Boolean
    
    /**
     * Delete a consumption item by id and business unit id
     */
    suspend fun deleteByIdAndBusinessUnitId(id: String, businessUnitId: String): Long
}
