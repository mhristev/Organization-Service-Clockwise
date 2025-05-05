package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.repositories.CompanyRepository
import com.clockwise.orgservice.service.CompanyService
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Service
class CompanyServiceImpl(private val companyRepository: CompanyRepository): CompanyService {
    override suspend fun createCompany(company: Company): Company {
        logger.debug { "Creating company: $company" }
        val savedCompany = companyRepository.save(company)
        logger.info { "Created company with ID: ${savedCompany.id}" }
        return savedCompany
    }

    override suspend fun getCompanyById(id: String): Company? {
        logger.debug { "Fetching company with ID: $id" }
        val company = companyRepository.findById(id)
        if (company == null) {
            logger.debug { "No company found with ID: $id" }
        }
        return company
    }

    override suspend fun updateCompany(id: String, company: Company): Company {
        logger.debug { "Updating company with ID: $id" }
        val updatedCompany = companyRepository.save(company)
        logger.info { "Updated company with ID: ${updatedCompany.id}" }
        return updatedCompany
    }

    override suspend fun deleteCompany(id: String) {
        logger.debug { "Deleting company with ID: $id" }
        companyRepository.deleteById(id)
        logger.info { "Deleted company with ID: $id" }
    }

    override fun getAllCompanies(): Flow<Company> {
        return companyRepository.findAll()
    }
}