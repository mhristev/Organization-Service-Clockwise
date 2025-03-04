package com.clockwise.orgservice.repositories

import com.clockwise.orgservice.domain.Company
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompanyRepository : CoroutineCrudRepository<Company, String>


