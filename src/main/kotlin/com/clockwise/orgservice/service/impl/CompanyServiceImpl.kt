package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.config.DataError
import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.repositories.CompanyRepository
import com.clockwise.orgservice.service.CompanyService
import org.springframework.stereotype.Service
import com.clockwise.orgservice.config.Result
import org.springframework.dao.OptimisticLockingFailureException

@Service
class CompanyServiceImpl(private val companyRepository: CompanyRepository): CompanyService {
    override suspend fun createCompany(company: Company): Result<Company?, DataError> {
        return try {
            Result.Success(companyRepository.save(company))
        }
        catch (e: IllegalArgumentException) {
            Result.Error(DataError.Remote.INVALID_INPUT)
        } catch (e: OptimisticLockingFailureException) {
            Result.Error(DataError.Remote.CONFLICT)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getCompanyById(id: String): Result<Company?, DataError> {
        return try {
            Result.Success(companyRepository.findById(id))
        } catch (e: IllegalArgumentException) {
            Result.Error(DataError.Remote.INVALID_INPUT)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun updateCompany(id: String, company: Company): Result<Company?, DataError> {
        return try {
            Result.Success(companyRepository.save(company))
        }
        catch (e: IllegalArgumentException) {
            Result.Error(DataError.Remote.INVALID_INPUT)
        } catch (e: OptimisticLockingFailureException) {
            Result.Error(DataError.Remote.CONFLICT)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun deleteCompany(id: String): Result<Unit, DataError> {
        return try {
            companyRepository.deleteById(id)
            Result.Success(Unit)
        } catch (e: IllegalArgumentException) {
            Result.Error(DataError.Remote.INVALID_INPUT)
        } catch (e: OptimisticLockingFailureException) {
            Result.Error(DataError.Remote.CONFLICT)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}