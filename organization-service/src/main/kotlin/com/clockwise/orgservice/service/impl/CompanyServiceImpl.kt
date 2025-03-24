package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.repositories.CompanyRepository
import com.clockwise.orgservice.service.CompanyService
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(private val companyRepository: CompanyRepository): CompanyService {
    override suspend fun createCompany(company: Company): Company {
        return companyRepository.save(company)
    }

    override suspend fun getCompanyById(id: String): Company? {
        return companyRepository.findById(id)
    }

    override suspend fun updateCompany(id: String, company: Company): Company {
        return companyRepository.save(company)
    }

    override suspend fun deleteCompany(id: String) {
       companyRepository.deleteById(id)
    }

    override fun getAllCompanies(): Flow<Company> {
        return companyRepository.findAll()
    }
}