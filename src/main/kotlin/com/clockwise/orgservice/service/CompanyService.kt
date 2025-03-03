package com.clockwise.orgservice.service

import com.clockwise.orgservice.config.DataError
import com.clockwise.orgservice.domain.Company
import org.springframework.stereotype.Service
import com.clockwise.orgservice.config.Result

@Service
interface CompanyService {
    suspend fun createCompany(company: Company): Result<Company?, DataError>
    suspend fun getCompanyById(id: String): Result<Company?, DataError>
    suspend fun updateCompany(id: String, company: Company): Result<Company?, DataError>
    suspend fun deleteCompany(id: String): Result<Unit, DataError>

}