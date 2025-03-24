package com.clockwise.orgservice.service

import com.clockwise.orgservice.domain.Company
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
interface CompanyService {
    suspend fun createCompany(company: Company): Company
    suspend fun getCompanyById(id: String): Company?
    suspend fun updateCompany(id: String, company: Company): Company
    suspend fun deleteCompany(id: String)
    fun getAllCompanies(): Flow<Company>
}