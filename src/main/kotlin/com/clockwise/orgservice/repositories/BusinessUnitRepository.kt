package com.clockwise.orgservice.repositories

import com.clockwise.orgservice.domain.BusinessUnit
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RestaurantRepository : CoroutineCrudRepository<BusinessUnit, String> {
    fun findByCompanyId(companyId: String): Flow<BusinessUnit>
}
